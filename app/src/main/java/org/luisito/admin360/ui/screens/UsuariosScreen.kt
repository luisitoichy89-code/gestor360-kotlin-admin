package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.data.models.User
import org.luisito.admin360.ui.components.BuscadorField
import org.luisito.admin360.ui.components.ConfirmarEliminarDialog
import org.luisito.admin360.ui.components.EstadoCargando
import org.luisito.admin360.ui.components.EstadoChip
import org.luisito.admin360.ui.components.EstadoError
import org.luisito.admin360.ui.components.EstadoVacio
import org.luisito.admin360.ui.viewmodels.UsuarioViewModel

private val ROLES = listOf("admin", "vendedor")

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

    LaunchedEffect(clienteId) {
        viewModel.loadUsuarios(clienteId)
    }

    val usuariosFiltrados = remember(uiState.usuarios, query) {
        if (query.isBlank()) uiState.usuarios
        else uiState.usuarios.filter {
            it.username.contains(query, ignoreCase = true) ||
                (it.nombre?.contains(query, ignoreCase = true) == true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Usuarios")
                        if (negocioNombre.isNotBlank()) {
                            Text(negocioNombre, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
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
                    usuarioEnEdicion = null
                    mostrarFormulario = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo usuario") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            BuscadorField(query = query, onQueryChange = { query = it }, placeholder = "Buscar usuario...")
            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> EstadoCargando()
                uiState.error != null -> EstadoError(uiState.error ?: "Error desconocido") { viewModel.refrescar() }
                usuariosFiltrados.isEmpty() -> EstadoVacio(
                    if (query.isBlank()) "Aún no hay usuarios registrados" else "Sin resultados para \"$query\""
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(usuariosFiltrados, key = { it.id }) { usuario ->
                        UsuarioCard(
                            usuario = usuario,
                            onEditar = {
                                usuarioEnEdicion = usuario
                                mostrarFormulario = true
                            },
                            onToggleActivo = { viewModel.toggleActivo(usuario) },
                            onEliminar = { usuarioAEliminar = usuario }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (mostrarFormulario) {
        UsuarioFormDialog(
            usuario = usuarioEnEdicion,
            isSaving = uiState.isSaving,
            onDismiss = { mostrarFormulario = false },
            onGuardar = { username, nombre, password, rol, almacenId, activo ->
                val existente = usuarioEnEdicion
                if (existente == null) {
                    viewModel.createUsuario(username, nombre, password, rol, clienteId, almacenId)
                } else {
                    viewModel.updateUsuario(existente.id, username, nombre, rol, almacenId, activo)
                }
                mostrarFormulario = false
            }
        )
    }

    usuarioAEliminar?.let { usuario ->
        ConfirmarEliminarDialog(
            nombre = usuario.nombre ?: usuario.username,
            onConfirm = {
                viewModel.deleteUsuario(usuario.id)
                usuarioAEliminar = null
            },
            onDismiss = { usuarioAEliminar = null }
        )
    }
}

@Composable
private fun UsuarioCard(
    usuario: User,
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
                Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre?.takeIf { it.isNotBlank() } ?: usuario.username, style = MaterialTheme.typography.titleMedium)
                Text("@${usuario.username} · ${usuario.rol}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                EstadoChip(activo = usuario.activo)
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
                        text = { Text(if (usuario.activo) "Desactivar" else "Activar") },
                        leadingIcon = {
                            Icon(
                                if (usuario.activo) Icons.Default.ToggleOff else Icons.Default.ToggleOn,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsuarioFormDialog(
    usuario: User?,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onGuardar: (username: String, nombre: String, password: String, rol: String, almacenId: String, activo: Boolean) -> Unit
) {
    var username by remember { mutableStateOf(usuario?.username ?: "") }
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rol by remember { mutableStateOf(usuario?.rol ?: ROLES.first()) }
    var rolMenuAbierto by remember { mutableStateOf(false) }
    var almacenId by remember { mutableStateOf(usuario?.almacen_id ?: "") }
    var activo by remember { mutableStateOf(usuario?.activo ?: true) }

    val esEdicion = usuario != null
    val formularioValido = username.isNotBlank() && (esEdicion || password.length >= 6)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (esEdicion) "Editar usuario" else "Nuevo usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!esEdicion) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña (mín. 6 caracteres)") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(expanded = rolMenuAbierto, onExpandedChange = { rolMenuAbierto = it }) {
                    OutlinedTextField(
                        value = rol,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rolMenuAbierto) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = rolMenuAbierto, onDismissRequest = { rolMenuAbierto = false }) {
                        ROLES.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = { rol = opcion; rolMenuAbierto = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = almacenId,
                    onValueChange = { almacenId = it },
                    label = { Text("ID de local / almacén") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
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
            TextButton(
                enabled = formularioValido && !isSaving,
                onClick = {
                    onGuardar(username.trim(), nombre.trim(), password, rol, almacenId.trim(), activo)
                }
            ) {
                Text(if (isSaving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
