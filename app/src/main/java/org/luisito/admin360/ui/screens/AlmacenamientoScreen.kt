package org.luisito.admin360.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import org.luisito.admin360.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.postgrest

@Serializable
data class EspacioNegocio(
    val negocio_id: String, val nombre_negocio: String,
    val productos: Long = 0, val ventas: Long = 0, val usuarios: Long = 0,
    val turnos: Long = 0, val trazas: Long = 0,
    val total_registros: Long = 0, val espacio_estimado_mb: Double = 0.0
)

data class AlmacenamientoUiState(
    val isLoading: Boolean = false,
    val negocios: List<EspacioNegocio> = emptyList(),
    val totalUsadoMb: Double = 0.0,
    val totalMb: Double = 500.0,
    val error: String? = null
)

class AlmacenamientoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AlmacenamientoUiState())
    val uiState: StateFlow<AlmacenamientoUiState> = _uiState.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val negocios = SupabaseProvider.client.postgrest.rpc("get_espacio_negocios").decodeList<EspacioNegocio>()
                val total = SupabaseProvider.client.postgrest.rpc("get_storage_public_mb").decodeAs<Double>()
                _uiState.value = _uiState.value.copy(isLoading = false, negocios = negocios, totalUsadoMb = total)
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlmacenamientoScreen(onBack: () -> Unit, viewModel: AlmacenamientoViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.cargar() }
    val libre = state.totalMb - state.totalUsadoMb
    val porcentaje = if (state.totalMb > 0) (state.totalUsadoMb / state.totalMb).toFloat() else 0f
    val alerta = when { porcentaje >= 0.95f -> "CRÍTICO: almacenamiento casi lleno"; porcentaje >= 0.85f -> "ALTO: revisa espacio disponible"; porcentaje >= 0.70f -> "ADVERTENCIA: uso elevado"; else -> null }
    val top = state.negocios.sortedByDescending { it.espacio_estimado_mb }.take(5)

    Scaffold(topBar = { TopAppBar(title = { Text("Storage Dashboard") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Uso total del sistema", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Usado: %.1f MB".format(state.totalUsadoMb)); Text("Libre: %.1f MB".format(libre)) }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { porcentaje }, modifier = Modifier.fillMaxWidth().height(10.dp))
                    Spacer(Modifier.height(6.dp))
                    Text("Total: %.0f MB".format(state.totalMb))
                }
            }
            if (alerta != null) { Spacer(Modifier.height(12.dp)); Card(colors = CardDefaults.cardColors(containerColor = when { porcentaje >= 0.95f -> MaterialTheme.colorScheme.errorContainer; porcentaje >= 0.85f -> MaterialTheme.colorScheme.tertiaryContainer; else -> MaterialTheme.colorScheme.primaryContainer })) { Text(alerta, Modifier.padding(12.dp), fontWeight = FontWeight.Bold) } }
            Spacer(Modifier.height(16.dp))
            Text("Top consumo de almacenamiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                top.forEach { n ->
                    val max = top.maxOfOrNull { it.espacio_estimado_mb } ?: 1.0; val ratio = (n.espacio_estimado_mb / max).toFloat()
                    Column { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(n.nombre_negocio, Modifier.weight(1f)); Text("%.1f MB".format(n.espacio_estimado_mb)) }; Spacer(Modifier.height(4.dp)); Box(Modifier.fillMaxWidth().height(10.dp).background(MaterialTheme.colorScheme.surfaceVariant)) { Box(Modifier.fillMaxHeight().fillMaxWidth(ratio).background(when { ratio > 0.8f -> MaterialTheme.colorScheme.error; ratio > 0.5f -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.primary })) } }
                }
            }
            Spacer(Modifier.height(16.dp))
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.error!!, color = MaterialTheme.colorScheme.error) }
                state.negocios.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Sin datos de almacenamiento") }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(state.negocios) { n -> ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(n.nombre_negocio, fontWeight = FontWeight.Bold, Modifier.weight(1f)); Text("%.1f MB".format(n.espacio_estimado_mb), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(6.dp)); Text("📦 ${n.productos}  🛒 ${n.ventas}  👤 ${n.usuarios}  ⏱ ${n.turnos}", style = MaterialTheme.typography.bodySmall); Text("Total registros: ${n.total_registros}", style = MaterialTheme.typography.bodySmall) } } } }
            }
        }
    }
}
