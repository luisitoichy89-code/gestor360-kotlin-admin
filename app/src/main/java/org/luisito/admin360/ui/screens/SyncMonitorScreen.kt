@file:OptIn(ExperimentalMaterial3Api::class)
package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.remote.SupabaseClientProvider

@Serializable
data class SyncQueueItem(
    val id: Long,
    val local_id: Long,
    val tipo: String,
    val estado: String,
    val intentos: Int = 0,
    val ultimo_error: String? = null,
    val creado_en: String? = null
)

data class SyncMonitorUiState(
    val isLoading: Boolean = false,
    val items: List<SyncQueueItem> = emptyList(),
    val totalPendientes: Int = 0,
    val totalError: Int = 0,
    val totalExitosas: Int = 0,
    val error: String? = null
)

class SyncMonitorViewModel : ViewModel() {
    private val _s = MutableStateFlow(SyncMonitorUiState())
    val uiState: StateFlow<SyncMonitorUiState> = _s.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _s.value = _s.value.copy(isLoading = true, error = null)
            try {
                val items = SupabaseClientProvider.client.postgrest.rpc("get_sync_queue")
                    .decodeList<SyncQueueItem>()
                _s.value = _s.value.copy(
                    isLoading = false,
                    items = items,
                    totalPendientes = items.count { it.estado == "pendiente" },
                    totalError = items.count { it.estado == "error" },
                    totalExitosas = items.count { it.estado == "sincronizado" }
                )
            } catch (e: Exception) {
                _s.value = _s.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun cancelar(id: Long) {
        viewModelScope.launch {
            try {
                SupabaseClientProvider.client.postgrest.rpc(
                    "resolver_sync",
                    buildJsonObject {
                        put("p_id", id)
                        put("p_estado", "cancelado")
                        put("p_error", "Cancelado por admin")
                    }
                )
                cargar()
            } catch (e: Exception) {
                _s.value = _s.value.copy(error = e.message)
            }
        }
    }
}

@Composable
fun SyncMonitorScreen(onBack: () -> Unit, vm: SyncMonitorViewModel = viewModel()) {
    val s by vm.uiState.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitor de Sincronización") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = { vm.cargar() }) { Icon(Icons.Default.Refresh, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MonitorCard("Pendientes", s.totalPendientes, MaterialTheme.colorScheme.error)
                MonitorCard("Error", s.totalError, MaterialTheme.colorScheme.tertiary)
                MonitorCard("Éxito", s.totalExitosas, MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))

            when {
                s.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                s.error != null -> Text(s.error!!, color = MaterialTheme.colorScheme.error)
                s.items.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay acciones registradas") }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(s.items) { item -> SyncQueueCard(item) { vm.cancelar(item.id) } }
                }
            }
        }
    }
}

@Composable
private fun MonitorCard(titulo: String, cantidad: Int, color: androidx.compose.ui.graphics.Color) {
    ElevatedCard(Modifier.weight(1f)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$cantidad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall, color = color)
            Text(titulo, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun SyncQueueCard(item: SyncQueueItem, onCancelar: () -> Unit) {
    ElevatedCard {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item.tipo.replace("_", " "), fontWeight = FontWeight.Bold)
                AssistChip(
                    onClick = {},
                    label = { Text(item.estado) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (item.estado) {
                            "pendiente" -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            "error" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        },
                        labelColor = when (item.estado) {
                            "pendiente" -> MaterialTheme.colorScheme.error
                            "error" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("Local: ${item.local_id}  ·  Intentos: ${item.intentos}", style = MaterialTheme.typography.bodySmall)
            if (item.ultimo_error != null) {
                Text(item.ultimo_error, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            if (item.estado != "sincronizado" && item.estado != "cancelado") {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onCancelar, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Icon(Icons.Default.Cancel, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Cancelar")
                }
            }
        }
    }
}
