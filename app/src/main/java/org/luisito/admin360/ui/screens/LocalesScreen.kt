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
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.ui.components.BuscadorField
import org.luisito.admin360.ui.components.ConfirmarEliminarDialog
import org.luisito.admin360.ui.components.EstadoChip
import org.luisito.admin360.ui.components.EstadoCargando
import org.luisito.admin360.ui.components.EstadoError
import org.luisito.admin360.ui.components.EstadoVacio
import org.luisito.admin360.ui.viewmodels.LocalViewModel

private val BG = MaterialTheme.colorScheme.background
private val CARD = MaterialTheme.colorScheme.surface
private val ACCENT = MaterialTheme.colorScheme.primary
private val TEXT = MaterialTheme.colorScheme.onSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalesScreen(negocioId: String, negocioNombre: String = "", onBack: (() -> Unit)? = null, viewModel: LocalViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }; var edit by remember { mutableStateOf<Local?>(null) }; var showForm by remember { mutableStateOf(false) }; var deleteItem by remember { mutableStateOf<Local?>(null) }
    LaunchedEffect(negocioId) { viewModel.loadLocales(negocioId) }
    val filtered = uiState.locales.filter { query.isBlank() || it.nombre.contains(query, true) }

    Scaffold(containerColor = BG, topBar = { TopAppBar(title = { Column { Text("Locales", color = TEXT); if (negocioNombre.isNotBlank()) Text(negocioNombre, color = ACCENT) } }, navigationIcon = onBack?.let { { IconButton(onClick = it) { Icon(Icons.Default.ArrowBack, null, tint = ACCENT) } } } ?: {}, colors = TopAppBarDefaults.topAppBarColors(containerColor = BG)) }, floatingActionButton = { FloatingActionButton(containerColor = ACCENT, onClick = { edit = null; showForm = true }) { Icon(Icons.Default.Add, null) } }) { padding ->
        Column(Modifier.fillMaxSize().background(BG).padding(padding).padding(16.dp)) {
            BuscadorField(query = query, onQueryChange = { query = it }, placeholder = "Buscar local...")
            Spacer(Modifier.height(12.dp))
            when { uiState.isLoading -> EstadoCargando(); uiState.error != null -> EstadoError(uiState.error ?: "") { viewModel.refrescar() }; filtered.isEmpty() -> EstadoVacio(if (query.isBlank()) "Este negocio aún no tiene locales" else "Sin resultados"); else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(filtered, key = { it.id }) { local -> ElevatedCard(Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = CARD)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Storefront, null, tint = ACCENT); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(local.nombre, color = TEXT); EstadoChip(activo = local.activo) }; IconButton(onClick = { edit = local; showForm = true }) { Icon(Icons.Default.Edit, null, tint = ACCENT) }; IconButton(onClick = { deleteItem = local }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }; IconButton(onClick = { viewModel.toggleActivo(local) }) { Icon(if (local.activo) Icons.Default.ToggleOn else Icons.Default.ToggleOff, null, tint = ACCENT) } } } }; item { Spacer(Modifier.height(80.dp)) } } }
        }
    }

    if (showForm) LocalFormDialog(edit, uiState.isSaving, { showForm = false }) { name -> if (edit == null) viewModel.createLocal(name, negocioId) else viewModel.updateLocal(edit!!.id, name); showForm = false }
    deleteItem?.let { local -> ConfirmarEliminarDialog(local.nombre, { viewModel.deleteLocal(local.id); deleteItem = null }, { deleteItem = null }) }
}

@Composable
private fun LocalFormDialog(local: Local?, isSaving: Boolean, onDismiss: () -> Unit, onGuardar: (String) -> Unit) {
    var nombre by remember { mutableStateOf(local?.nombre ?: "") }
    AlertDialog(containerColor = CARD, onDismissRequest = onDismiss, title = { Text(if (local == null) "Nuevo local" else "Editar local", color = TEXT) }, text = { OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre", color = TEXT) }) }, confirmButton = { TextButton(enabled = nombre.isNotBlank() && !isSaving, onClick = { onGuardar(nombre.trim()) }) { Text(if (isSaving) "Guardando..." else "Guardar", color = ACCENT) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant) } })
}
