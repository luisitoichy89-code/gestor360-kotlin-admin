package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.ui.components.BuscadorField
import org.luisito.admin360.ui.components.ConfirmarEliminarDialog
import org.luisito.admin360.ui.components.EstadoCargando
import org.luisito.admin360.ui.components.EstadoChip
import org.luisito.admin360.ui.components.EstadoError
import org.luisito.admin360.ui.components.EstadoVacio
import org.luisito.admin360.ui.viewmodels.NegocioViewModel

/**
 * Vista principal del superadmin: lista TODOS los negocios (tabla "clientes").
 * Al tocar uno, [onSeleccionarNegocio] lo marca como "negocio activo" en AppContent,
 * que luego se usa como contexto para Locales, Usuarios y Licencias.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegociosScreen(
    onSeleccionarNegocio: (Negocio) -> Unit = {},
    onBack: (() -> Unit)? = null,
    viewModel: NegocioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var negocioEnEdicion by remember { mutableStateOf<Negocio?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var negocioAEliminar by remember { mutableStateOf<Negocio?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTodosNegocios()
    }

    val negociosFiltrados = remember(uiState.negocios, query) {
        if (query.isBlank()) uiState.negocios
        else uiState.negocios.filter { it.nombre_negocio.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Negocios") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
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
                    negocioEnEdicion = null
                    mostrarFormulario = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo negocio") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Toca un negocio para gestionar sus locales, usuarios y licencias",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            BuscadorField(query = query, onQueryChange = { query = it }, placeholder = "Buscar negocio...")
            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> EstadoCargando()
                uiState.error != null -> EstadoError(uiState.error ?: "Error desconocido") { viewModel.refrescar() }
                negociosFiltrados.isEmpty() -> EstadoVacio(
                    if (query.isBlank()) "Aún no hay negocios registrados" else "Sin resultados para \"$query\""
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(negociosFiltrados, key = { it.id }) { negocio ->
                        NegocioCard(
                            negocio = negocio,
                            onClick = { onSeleccionarNegocio(negocio) },
                            onEditar = {
                                negocioEnEdicion = negocio
                                mostrarFormulario = true
                            },
                            onToggleActivo = { viewModel.toggleActivo(negocio) },
                            onEliminar = { negocioAEliminar = negocio }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (mostrarFormulario) {
        NegocioFormDialog(
            negocio = negocioEnEdicion,
            isSaving = uiState.isSaving,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { nombre ->
                val existente = negocioEnEdicion
                if (existente == null) {
                    viewModel.createNegocio(nombre)
                } else {
                    viewModel.updateNegocio(existente.id, nombre)
                }
                mostrarFormulario = false
            }
        )
    }

    negocioAEliminar?.let { negocio ->
        ConfirmarEliminarDialog(
            nombre = negocio.nombre_negocio,
            onConfirm = {
                viewModel.deleteNegocio(negocio.id)
                negocioAEliminar = null
            },
            onDismiss = { negocioAEliminar = null }
        )
    }
}

@Composable
private fun NegocioCard(
    negocio: Negocio,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onToggleActivo: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Storefront,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(negocio.nombre_negocio, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                EstadoChip(activo = negocio.activo)
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
                        text = { Text(if (negocio.activo) "Desactivar" else "Activar") },
                        leadingIcon = {
                            Icon(
                                if (negocio.activo) Icons.Default.ToggleOff else Icons.Default.ToggleOn,
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
private fun NegocioFormDialog(
    negocio: Negocio?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String) -> Unit
) {
    var nombre by remember { mutableStateOf(negocio?.nombre_negocio ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (negocio == null) "Nuevo negocio" else "Editar negocio") },
        text = {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del negocio") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank() && !isSaving,
                onClick = { onGuardar(nombre.trim()) }
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
