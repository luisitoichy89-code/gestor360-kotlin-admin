package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.repository.LocalRepository
import org.luisito.admin360.ui.components.ConfirmarEliminarDialog
import org.luisito.admin360.ui.components.EstadoCargando
import org.luisito.admin360.ui.components.EstadoChip
import org.luisito.admin360.ui.components.EstadoError
import org.luisito.admin360.ui.components.EstadoVacio
import org.luisito.admin360.ui.viewmodels.UsuarioViewModel

private val ROLES = listOf("admin", "seller")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    clienteId: String,
    negocioNombre: String = "",
    onBack: (() -> Unit)? = null,
    viewModel: UsuarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    var usuarioEnEdicion by remember { mutableStateOf<User?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioAEliminar by remember { mutableStateOf<User?>(null) }
    var usuarioParaCambiarPin by remember { mutableStateOf<User?>(null) }
    var nuevoPin by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val localRepo = remember { LocalRepository() }
    var locales by remember { mutableStateOf<List<Local>>(emptyList()) }

    LaunchedEffect(clienteId) {
        viewModel.loadUsuarios(clienteId)
        scope.launch { localRepo.getLocales(clienteId).onSuccess { locales = it } }
    }

    val usuariosFiltrados = uiState.usuarios.filter {
        query.isBlank() || it.username.contains(query, true) || (it.nombre?.contains(query, true) == true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (negocioNombre.isNotBlank()) "Usuarios · $negocioNombre" else "Usuarios") },
                navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } },
                actions = { IconButton(onClick = { viewModel.refrescar() }) { Icon(Icons.Default.Refresh, "Refrescar") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { usuarioEnEdicion = null; mostrarFormulario = true }) {
                Icon(Icons.Default.Add, "Crear usuario")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 14.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                label = { Text("Buscar usuario...") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> EstadoCargando()
                uiState.error != null -> EstadoError(uiState.error!!) { viewModel.refrescar() }
                usuariosFiltrados.isEmpty() -> EstadoVacio(mensaje = "No hay usuarios registrados")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(usuariosFiltrados, key = { it.id }) { usuario ->
                        val localNombre = if (usuario.rol == "admin") "Todos" else {
                            locales.find { it.id.toString() == usuario.almacen_id }?.nombre ?: "Local ${usuario.almacen_id}"
                        }
                        var menuAbierto by remember { mutableStateOf(false) }
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("👤 ${usuario.username}", style = MaterialTheme.typography.titleMedium)
                                    Text("🏪 $localNombre · ${usuario.rol}", style = MaterialTheme.typography.bodySmall)
                                    EstadoChip(activo = usuario.activo)
                                }
                                Box {
                                    IconButton(onClick = { menuAbierto = true }) {
                                        Icon(Icons.Default.MoreVert, "Más opciones")
                                    }
                                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                                        DropdownMenuItem(
                                            text = { Text("Editar") },
                                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                                            onClick = { menuAbierto = false; usuarioEnEdicion = usuario; mostrarFormulario = true }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(if (usuario.activo) "Desactivar" else "Activar") },
                                            leadingIcon = { Icon(if (usuario.activo) Icons.Default.ToggleOff else Icons.Default.ToggleOn, null) },
                                            onClick = { menuAbierto = false; viewModel.toggleActivo(usuario) }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Cambiar PIN") },
                                            leadingIcon = { Icon(Icons.Default.Key, null) },
                                            onClick = { menuAbierto = false; usuarioParaCambiarPin = usuario; nuevoPin = "" }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Eliminar") },
                                            leadingIcon = { Icon(Icons.Default.Delete, null) },
                                            onClick = { menuAbierto = false; usuarioAEliminar = usuario }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Diálogos ---
    if (mostrarFormulario) {
        UsuarioFormDialog(
            usuario = usuarioEnEdicion,
            isSaving = uiState.isSaving,
            locales = locales,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { username, nombre, pin, rol, almacenId, activo ->
                if (usuarioEnEdicion != null)
                    viewModel.updateUsuario(usuarioEnEdicion!!.id, username, nombre, rol, almacenId, activo)
                else
                    viewModel.createUsuario(username, nombre, pin, rol, clienteId, almacenId, deviceId)
                mostrarFormulario = false
            }
        )
    }

    usuarioAEliminar?.let { usuario ->
        ConfirmarEliminarDialog(
            nombre = usuario.username,
            onConfirm = { viewModel.deleteUsuario(usuario.id); usuarioAEliminar = null },
            onDismiss = { usuarioAEliminar = null }
        )
    }

    usuarioParaCambiarPin?.let { usuario ->
        AlertDialog(
            onDismissRequest = { usuarioParaCambiarPin = null },
            title = { Text("Cambiar PIN · ${usuario.username}") },
            text = {
                OutlinedTextField(
                    value = nuevoPin,
                    onValueChange = { if (it.length <= 6) nuevoPin = it.filter { c -> c.isDigit() } },
                    label = { Text("Nuevo PIN (4 a 6 dígitos)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(enabled = nuevoPin.length in 4..6, onClick = {
                    viewModel.cambiarPin(usuario.id, nuevoPin)
                    usuarioParaCambiarPin = null
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { usuarioParaCambiarPin = null }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsuarioFormDialog(
    usuario: User?,
    isSaving: Boolean,
    locales: List<Local>,
    onDismiss: () -> Unit,
    onGuardar: (username: String, nombre: String, pin: String, rol: String, almacenId: String, deviceId: String, activo: Boolean) -> Unit
) {
    var username by remember { mutableStateOf(usuario?.username ?: "") }
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var pin by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(usuario?.rol ?: "seller") }
    var almacenId by remember { mutableStateOf(usuario?.almacen_id ?: "1") }
    var deviceId by remember { mutableStateOf(usuario?.device_id ?: "") }
    var activo by remember { mutableStateOf(usuario?.activo ?: true) }
    var rolMenuAbierto by remember { mutableStateOf(false) }
    var localMenuAbierto by remember { mutableStateOf(false) }
    val esEdicion = usuario != null
    val pinValido = pin.length in 4..6 && pin.all { it.isDigit() }
    val formularioValido = username.isNotBlank() && (esEdicion || pinValido)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (esEdicion) "Editar usuario" else "Nuevo usuario") },
        text = {
            Column {
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre completo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                if (!esEdicion) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pin, onValueChange = { if (it.length <= 6) pin = it.filter { c -> c.isDigit() } },
                        label = { Text("PIN (4 a 6 dígitos)") }, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Dropdown de Rol
                ExposedDropdownMenuBox(expanded = rolMenuAbierto, onExpandedChange = { rolMenuAbierto = it }) {
                    OutlinedTextField(value = rol, onValueChange = {}, readOnly = true, label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rolMenuAbierto) },
                        modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = rolMenuAbierto, onDismissRequest = { rolMenuAbierto = false }) {
                        ROLES.forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion) }, onClick = { rol = opcion; rolMenuAbierto = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = deviceId, onValueChange = { deviceId = it }, label = { Text("Android ID del dispositivo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                // Dropdown de Local (solo vendedor)
                if (rol == "seller") {
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(expanded = localMenuAbierto, onExpandedChange = { localMenuAbierto = it }) {
                        val localSeleccionado = locales.find { it.id.toString() == almacenId }
                        OutlinedTextField(
                            value = localSeleccionado?.nombre ?: "Seleccionar local...",
                            onValueChange = {}, readOnly = true,
                            label = { Text("Local asignado") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = localMenuAbierto) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = localMenuAbierto, onDismissRequest = { localMenuAbierto = false }) {
                            if (locales.isEmpty()) {
                                DropdownMenuItem(text = { Text("No hay locales disponibles") }, onClick = { localMenuAbierto = false })
                            } else {
                                locales.forEach { local ->
                                    DropdownMenuItem(
                                        text = { Text("🏪 ${local.nombre}") },
                                        onClick = { almacenId = local.id.toString(); localMenuAbierto = false }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LaunchedEffect(rol) { if (rol == "admin") almacenId = "1" }
                }
                if (esEdicion) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = activo, onCheckedChange = { activo = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (activo) "Activo" else "Inactivo")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = formularioValido && !isSaving, onClick = {
                onGuardar(username.trim(), nombre.trim(), pin, rol, almacenId, deviceId, activo)
            }) { Text(if (isSaving) "Guardando..." else "Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
