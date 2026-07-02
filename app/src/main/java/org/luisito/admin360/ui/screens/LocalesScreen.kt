package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.ui.components.BuscadorField
import org.luisito.admin360.ui.components.ConfirmarEliminarDialog
import org.luisito.admin360.ui.components.EstadoCargando
import org.luisito.admin360.ui.components.EstadoChip
import org.luisito.admin360.ui.components.EstadoError
import org.luisito.admin360.ui.components.EstadoVacio
import org.luisito.admin360.ui.viewmodels.LocalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalesScreen(
    negocioId: String,
    viewModel: LocalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var localEnEdicion by remember { mutableStateOf<Local?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var localAEliminar by remember { mutableStateOf<Local?>(null) }

    LaunchedEffect(negocioId) {
        viewModel.loadLocales(negocioId)
    }

    val localesFiltrados = remember(uiState.locales, query) {
        if (query.isBlank()) uiState.locales
        else uiState.locales.filter { it.nombre.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locales") },
                actions = {
                    IconButton(onClick = { viewModel.refrescar() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    localEnEdicion = null
                    mostrarFormulario = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo local") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            BuscadorField(query = query, onQueryChange = { query = it }, placeholder = "Buscar local...")
            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> EstadoCargando()
                uiState.error != null -> EstadoError(uiState.error ?: "Error desconocido") { viewModel.refrescar() }
                localesFiltrados.isEmpty() -> EstadoVacio(
                    if (query.isBlank()) "Este negocio aún no tiene locales" else "Sin resultados para \"$query\""
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(localesFiltrados, key = { it.id }) { local ->
                        LocalCard(
                            local = local,
                            onEditar = {
                                localEnEdicion = local
                                mostrarFormulario = true
                            },
                            onToggleActivo = { viewModel.toggleActivo(local) },
                            onEliminar = { localAEliminar = local }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (mostrarFormulario) {
        LocalFormDialog(
            local = localEnEdicion,
            isSaving = uiState.isSaving,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { nombre, direccion, telefono ->
                val existente = localEnEdicion
                if (existente == null) {
                    viewModel.createLocal(nombre, direccion, telefono, negocioId)
                } else {
                    viewModel.updateLocal(existente.id, nombre, direccion, telefono)
                }
                mostrarFormulario = false
            }
        )
    }

    localAEliminar?.let { local ->
        ConfirmarEliminarDialog(
            nombre = local.nombre,
            onConfirm = {
                viewModel.deleteLocal(local.id)
                localAEliminar = null
            },
            onDismiss = { localAEliminar = null }
        )
    }
}

@Composable
private fun LocalCard(
    local: Local,
    onEditar: () -> Unit,
    onToggleActivo: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Storefront,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(local.nombre, style = MaterialTheme.typography.titleMedium)
                if (!local.direccion.isNullOrBlank()) {
                    Text(local.direccion, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                EstadoChip(activo = local.activo)
            }
            Box {
                IconButton(onClick = { menuAbierto = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = { menuAbierto = false; onEditar() }
                    )
                    DropdownMenuItem(
                        text = { Text(if (local.activo) "Desactivar" else "Activar") },
                        leadingIcon = {
                            Icon(
                                if (local.activo) Icons.Default.ToggleOff else Icons.Default.ToggleOn,
                                contentDescription = null
                            )
                        },
                        onClick = { menuAbierto = false; onToggleActivo() }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = { menuAbierto = false; onEliminar() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalFormDialog(
    local: Local?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, direccion: String, telefono: String) -> Unit
) {
    var nombre by remember { mutableStateOf(local?.nombre ?: "") }
    var direccion by remember { mutableStateOf(local?.direccion ?: "") }
    var telefono by remember { mutableStateOf(local?.telefono ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (local == null) "Nuevo local" else "Editar local") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank() && !isSaving,
                onClick = { onGuardar(nombre.trim(), direccion.trim(), telefono.trim()) }
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
