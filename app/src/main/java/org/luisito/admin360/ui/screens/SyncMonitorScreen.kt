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
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.remote.SupabaseProvider

@Serializable
data class SyncQueueItem(
    val id: Long,
    val local_id: Long,
    val tipo: String,
    val estado: String,
    val intentos: Int = 0,
    val ultimo_error: String? = null,
    val creado_en: String? = null
) {
    val puedeReintentar: Boolean get() = estado in listOf("pendiente", "error") && intentos < 5 && !tipo.startsWith("eliminar")
    val puedeCancelar: Boolean get() = estado in listOf("pendiente", "error")
}

data class SyncMonitorUiState(
    val isLoading: Boolean = false,
    val items: List<SyncQueueItem> = emptyList(),
    val totalPendientes: Int = 0,
    val totalError: Int = 0,
    val totalExitosas: Int = 0,
    val mostrarResueltos: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

class SyncMonitorViewModel : ViewModel() {
    private val _s = MutableStateFlow(SyncMonitorUiState())
    val uiState: StateFlow<SyncMonitorUiState> = _s.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _s.value = _s.value.copy(isLoading = true, error = null)
            try {
                val items = SupabaseProvider.client.postgrest.rpc("get_sync_queue")
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

    fun toggleResueltos() {
        _s.value = _s.value.copy(mostrarResueltos = !_s.value.mostrarResueltos)
    }

    fun reintentar(id: Long) {
        viewModelScope.launch {
            try {
                SupabaseProvider.client.postgrest.rpc("reintentar_sync", buildJsonObject { put("p_id", id) })
                _s.value = _s.value.copy(mensaje = "Reintentado con éxito")
                cargar()
            } catch (e: Exception) {
                _s.value = _s.value.copy(error = e.message)
            }
        }
    }

    fun cancelar(id: Long) {
        viewModelScope.launch {
            try {
                SupabaseProvider.client.postgrest.rpc("resolver_sync", buildJsonObject {
                    put("p_id", id); put("p_estado", "cancelado"); put("p_error", "Cancelado por admin")
                })
                _s.value = _s.value.copy(mensaje = "Cancelado")
                cargar()
            } catch (e: Exception) {
                _s.value = _s.value.copy(error = e.message)
            }
        }
    }

    fun limpiarSincronizadas() {
        viewModelScope.launch {
            try {
                SupabaseProvider.client.postgrest.rpc("limpiar_sync_queue")
                _s.value = _s.value.copy(mensaje = "Cola limpia")
                cargar()
            } catch (e: Exception) {
                _s.value = _s.value.copy(error = e.message)
            }
        }
    }

    fun clearMensaje() { _s.value = _s.value.copy(mensaje = null) }
    fun clearError() { _s.value = _s.value.copy(error = null) }
}

@Composable
fun SyncMonitorScreen(onBack: () -> Unit, vm: SyncMonitorViewModel = viewModel()) {
    val s by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) { vm.cargar() }
    LaunchedEffect(s.mensaje) { s.mensaje?.let { snackbarHostState.showSnackbar(it); vm.clearMensaje() } }
    LaunchedEffect(s.error) { s.error?.let { snackbarHostState.showSnackbar(it); vm.clearError() } }

    val itemsFiltrados = if (s.mostrarResueltos) s.items else s.items.filter { it.estado in listOf("pendiente", "error") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitor de Sincronización") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    IconButton(onClick = { vm.toggleResueltos() }) {
                        Icon(
                            if (s.mostrarResueltos) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            "Ver resueltos"
                        )
                    }
                    IconButton(onClick = { vm.limpiarSincronizadas() }) { Icon(Icons.Default.CleaningServices, "Limpiar") }
                    IconButton(onClick = { vm.cargar() }) { Icon(Icons.Default.Refresh, null) }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ElevatedCard(Modifier.weight(1f)) {
                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${s.totalPendientes}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
                        Text("Pendientes", style = MaterialTheme.typography.labelSmall)
                    }
                }
                ElevatedCard(Modifier.weight(1f)) {
                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${s.totalError}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.tertiary)
                        Text("Error", style = MaterialTheme.typography.labelSmall)
                    }
                }
                ElevatedCard(Modifier.weight(1f)) {
                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${s.totalExitosas}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Text("Éxito", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            when {
                s.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                itemsFiltrados.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay acciones pendientes") }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(itemsFiltrados) { item -> SyncQueueCard(item, { vm.reintentar(item.id) }, { vm.cancelar(item.id) }) }
                }
            }
        }
    }
}

@Composable
private fun SyncQueueCard(item: SyncQueueItem, onReintentar: () -> Unit, onCancelar: () -> Unit) {
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
                            "cancelado" -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        },
                        labelColor = when (item.estado) {
                            "pendiente" -> MaterialTheme.colorScheme.error
                            "error" -> MaterialTheme.colorScheme.tertiary
                            "cancelado" -> MaterialTheme.colorScheme.outline
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
            if (item.puedeReintentar || item.puedeCancelar) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (item.puedeReintentar) {
                        TextButton(onClick = onReintentar, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                            Icon(Icons.Default.Refresh, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reintentar")
                        }
                    }
                    if (item.puedeCancelar) {
                        TextButton(onClick = onCancelar, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                            Icon(Icons.Default.Cancel, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}
