package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Storage
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
import org.luisito.admin360.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class EspacioNegocio(
    val negocio_id: String,
    val nombre_negocio: String,
    val productos: Long = 0,
    val ventas: Long = 0,
    val usuarios: Long = 0,
    val turnos: Long = 0,
    val trazas: Long = 0,
    val total_registros: Long = 0,
    val espacio_estimado_mb: Double = 0.0
)

data class AlmacenamientoUiState(
    val isLoading: Boolean = false,
    val negocios: List<EspacioNegocio> = emptyList(),
    val totalMb: Double = 500.0,
    val usadoMb: Double = 0.0,
    val error: String? = null
)

class AlmacenamientoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AlmacenamientoUiState())
    val uiState: StateFlow<AlmacenamientoUiState> = _uiState.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = SupabaseProvider.client.postgrest
                    .rpc("get_espacio_negocios")
                    .decodeList<EspacioNegocio>()
                val total = response.sumOf { it.espacio_estimado_mb }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    negocios = response,
                    usadoMb = total
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlmacenamientoScreen(
    onBack: () -> Unit,
    viewModel: AlmacenamientoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.cargar() }

    val filtrados = uiState.negocios.filter {
        query.isBlank() || it.nombre_negocio.contains(query, true)
    }

    val porcentajeUsado = if (uiState.totalMb > 0) (uiState.usadoMb / uiState.totalMb) else 0.0
    val libreMb = uiState.totalMb - uiState.usadoMb

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Almacenamiento") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Barra de progreso
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Usado: ${"%.1f".format(uiState.usadoMb)} MB", fontWeight = FontWeight.Bold)
                        Text("Libre: ${"%.1f".format(libreMb)} MB", color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { porcentajeUsado.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = if (porcentajeUsado > 0.8) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text("Total: ${"%.0f".format(uiState.totalMb)} MB", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buscador
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar negocio...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de negocios
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                uiState.error != null -> Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                filtrados.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Sin resultados") }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtrados) { negocio ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(negocio.nombre_negocio, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Text("${"%.1f".format(negocio.espacio_estimado_mb)} MB", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("📦 ${negocio.productos}  🛒 ${negocio.ventas}  👤 ${negocio.usuarios}  💰 ${negocio.turnos}  📋 ${negocio.trazas}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text("Total registros: ${negocio.total_registros}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
