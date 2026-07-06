package org.luisito.admin360.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private val BG = Color(0xFF070B1A)
private val CARD = Color(0xFF101A33)
private val ACCENT = Color(0xFF4DA3FF)
private val TEXT = Color(0xFFEAF0FF)

@Serializable
data class Negocio(val id: String, val nombre_negocio: String, val activo: Boolean = true)

data class NegocioUiState(val isLoading: Boolean = false, val isSaving: Boolean = false, val negocios: List<Negocio> = emptyList(), val error: String? = null)

class NegocioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NegocioUiState())
    val uiState: StateFlow<NegocioUiState> = _uiState.asStateFlow()

    fun loadTodosNegocios() { viewModelScope.launch { _uiState.value = _uiState.value.copy(isLoading = true, error = null); runCatching { SupabaseProvider.client.postgrest.from("clientes").select().decodeList<Negocio>() }.onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, negocios = it) }.onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) } } }
    fun createNegocio(nombre: String) { viewModelScope.launch { _uiState.value = _uiState.value.copy(isSaving = true); runCatching { SupabaseProvider.client.postgrest.from("clientes").insert(mapOf("nombre_negocio" to nombre)) }.onSuccess { loadTodosNegocios(); _uiState.value = _uiState.value.copy(isSaving = false) }.onFailure { _uiState.value = _uiState.value.copy(isSaving = false, error = it.message) } } }
    fun updateNegocio(id: String, nombre: String) { viewModelScope.launch { _uiState.value = _uiState.value.copy(isSaving = true); runCatching { SupabaseProvider.client.postgrest.from("clientes").update(mapOf("nombre_negocio" to nombre)).eq("id", id) }.onSuccess { loadTodosNegocios(); _uiState.value = _uiState.value.copy(isSaving = false) }.onFailure { _uiState.value = _uiState.value.copy(isSaving = false, error = it.message) } } }
    fun deleteNegocio(id: String) { viewModelScope.launch { runCatching { SupabaseProvider.client.postgrest.from("clientes").delete().eq("id", id) }.onSuccess { loadTodosNegocios() }.onFailure { _uiState.value = _uiState.value.copy(error = it.message) } } }
    fun toggleActivo(negocio: Negocio) { viewModelScope.launch { runCatching { SupabaseProvider.client.postgrest.from("clientes").update(mapOf("activo" to !negocio.activo)).eq("id", negocio.id) }.onSuccess { loadTodosNegocios() } } }
    fun refrescar() = loadTodosNegocios()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegociosScreen(onSeleccionarNegocio: (Negocio) -> Unit = {}, onBack: (() -> Unit)? = null, viewModel: NegocioViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }; var edit by remember { mutableStateOf<Negocio?>(null) }; var showForm by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { viewModel.loadTodosNegocios() }
    val filtered = state.negocios.filter { query.isBlank() || it.nombre_negocio.contains(query, true) }

    Scaffold(containerColor = BG, topBar = { TopAppBar(title = { Text("Negocios", color = TEXT) }, navigationIcon = onBack?.let { { IconButton(onClick = it) { Icon(Icons.Default.ArrowBack, null, tint = ACCENT) } } } ?: {}, colors = TopAppBarDefaults.topAppBarColors(containerColor = BG)) }, floatingActionButton = { FloatingActionButton(containerColor = ACCENT, onClick = { edit = null; showForm = true }) { Icon(Icons.Default.Add, null) } }) { padding ->
        Column(Modifier.fillMaxSize().background(BG).padding(padding).padding(16.dp)) {
            OutlinedTextField(query, { query = it }, label = { Text("Buscar", color = TEXT) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            when { state.isLoading -> CircularProgressIndicator(color = ACCENT); state.error != null -> Text(state.error ?: "", color = Color.Red); filtered.isEmpty() -> Text("Sin negocios", color = TEXT); else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(filtered) { n -> ElevatedCard(Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = CARD), onClick = { onSeleccionarNegocio(n) }) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Storefront, null, tint = ACCENT); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(n.nombre_negocio, color = TEXT); Text(if (n.activo) "Activo" else "Inactivo", color = ACCENT) }; IconButton(onClick = { edit = n; showForm = true }) { Icon(Icons.Default.Edit, null, tint = ACCENT) }; IconButton(onClick = { viewModel.deleteNegocio(n.id) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }; IconButton(onClick = { viewModel.toggleActivo(n) }) { Icon(if (n.activo) Icons.Default.ToggleOn else Icons.Default.ToggleOff, null, tint = ACCENT) } } } } } }
        }
    }

    if (showForm) NegocioForm(edit, state.isSaving, { showForm = false }) { name -> if (edit == null) viewModel.createNegocio(name) else viewModel.updateNegocio(edit!!.id, name); showForm = false }
}

@Composable
fun NegocioForm(negocio: Negocio?, isSaving: Boolean, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(negocio?.nombre_negocio ?: "") }
    AlertDialog(onDismissRequest = onDismiss, containerColor = CARD, title = { Text(if (negocio == null) "Nuevo negocio" else "Editar", color = TEXT) }, text = { OutlinedTextField(name, { name = it }, label = { Text("Nombre") }) }, confirmButton = { TextButton(enabled = name.isNotBlank() && !isSaving, onClick = { onSave(name.trim()) }) { Text(if (isSaving) "Guardando..." else "Guardar", color = ACCENT) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) } })
}
