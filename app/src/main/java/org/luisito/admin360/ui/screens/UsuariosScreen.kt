package org.luisito.admin360.ui.screens
import androidx.compose.material3.OutlinedTextFieldDefaults
import org.luisito.admin360.ui.theme.LineOrange

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.repository.LocalRepository
import org.luisito.admin360.ui.components.*
import org.luisito.admin360.ui.theme.NeumorphicShape
import org.luisito.admin360.ui.theme.neumorphicOnBackground
import org.luisito.admin360.ui.viewmodels.UsuarioViewModel

private val ROLES = listOf("admin", "seller")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(clienteId: String, negocioNombre: String = "", onBack: (() -> Unit)? = null, viewModel: UsuarioViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }; var usuarioEnEdicion by remember { mutableStateOf<User?>(null) }; var mostrarFormulario by remember { mutableStateOf(false) }
    var usuarioAEliminar by remember { mutableStateOf<User?>(null) }; var usuarioParaCambiarPin by remember { mutableStateOf<User?>(null) }; var nuevoPin by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope(); val localRepo = remember { LocalRepository() }; var locales by remember { mutableStateOf<List<Local>>(emptyList()) }
    LaunchedEffect(clienteId) { viewModel.loadUsuarios(clienteId); scope.launch { localRepo.getLocales(clienteId).onSuccess { locales = it } } }
    val usuariosFiltrados = uiState.usuarios.filter { query.isBlank() || it.username.contains(query, true) || (it.nombre?.contains(query, true) == true) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = { TopAppBar(title = { Text(if (negocioNombre.isNotBlank()) "Usuarios · $negocioNombre" else "Usuarios", color = MaterialTheme.colorScheme.onSurface) }, navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.primary) } }, actions = { IconButton(onClick = { viewModel.refrescar() }) { Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.primary) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, floatingActionButton = { FloatingActionButton(containerColor = MaterialTheme.colorScheme.primary, onClick = { usuarioEnEdicion = null; mostrarFormulario = true }) { Icon(Icons.Default.Add, null) } }) { padding ->
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding).padding(14.dp)) {
            OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = LineOrange, focusedTextColor = LineOrange, cursorColor = LineOrange, focusedBorderColor = LineOrange, unfocusedBorderColor = LineOrange.copy(alpha = 0.5f)))
            Spacer(Modifier.height(10.dp))
            when { uiState.isLoading -> EstadoCargando(); uiState.error != null -> EstadoError(uiState.error!!) { viewModel.refrescar() }; usuariosFiltrados.isEmpty() -> EstadoVacio("Sin usuarios"); else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(usuariosFiltrados, key = { it.id }) { usuario -> val localNombre = if (usuario.rol == "admin") "Todos" else locales.find { it.id.toString() == usuario.almacen_id }?.nombre ?: "Local ${usuario.almacen_id}"; var menuAbierto by remember { mutableStateOf(false) }; Surface(color = MaterialTheme.colorScheme.surface, shape = NeumorphicShape, modifier = Modifier.fillMaxWidth().neumorphicOnBackground()) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text("👤 ${usuario.username}", color = MaterialTheme.colorScheme.onSurface); Text("🏪 $localNombre · ${usuario.rol}", color = MaterialTheme.colorScheme.onSurfaceVariant); if (!usuario.android_id.isNullOrBlank()) Text("📱 ${usuario.android_id}", color = MaterialTheme.colorScheme.onSurfaceVariant); EstadoChip(activo = usuario.activo) }; Box { IconButton(onClick = { menuAbierto = true }) { Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.primary) }; DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) { DropdownMenuItem(text = { Text("Editar") }, onClick = { menuAbierto = false; usuarioEnEdicion = usuario; mostrarFormulario = true }); DropdownMenuItem(text = { Text(if (usuario.activo) "Desactivar" else "Activar") }, onClick = { menuAbierto = false; viewModel.toggleActivo(usuario) }); DropdownMenuItem(text = { Text("Cambiar PIN") }, onClick = { menuAbierto = false; usuarioParaCambiarPin = usuario; nuevoPin = "" }); DropdownMenuItem(text = { Text("Eliminar") }, onClick = { menuAbierto = false; usuarioAEliminar = usuario }) } } } } } } }
        }
    }

    if (mostrarFormulario) UsuarioFormDialog(usuarioEnEdicion, uiState.isSaving, locales, { mostrarFormulario = false }) { username, nombre, pin, rol, almacenId, androidId, activo -> if (usuarioEnEdicion != null) viewModel.updateUsuario(usuarioEnEdicion!!.id, username, nombre, rol, almacenId, androidId, activo) else viewModel.createUsuario(username, nombre, pin, rol, clienteId, almacenId, androidId); mostrarFormulario = false }
    usuarioAEliminar?.let { ConfirmarEliminarDialog(it.username, { viewModel.deleteUsuario(it.id); usuarioAEliminar = null }, { usuarioAEliminar = null }) }
    usuarioParaCambiarPin?.let { u -> AlertDialog(onDismissRequest = { usuarioParaCambiarPin = null }, title = { Text("Cambiar PIN") }, text = { OutlinedTextField(nuevoPin, { if (it.length <= 6) nuevoPin = it.filter { c -> c.isDigit() } }, label = { Text("PIN 4-6 dígitos") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)) }, confirmButton = { TextButton(enabled = nuevoPin.length in 4..6, onClick = { viewModel.cambiarPin(u.id, nuevoPin); usuarioParaCambiarPin = null }) { Text("Guardar") } }, dismissButton = { TextButton(onClick = { usuarioParaCambiarPin = null }) { Text("Cancelar") } }) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsuarioFormDialog(usuario: User?, isSaving: Boolean, locales: List<Local>, onDismiss: () -> Unit, onGuardar: (username: String, nombre: String, pin: String, rol: String, almacenId: String, androidId: String, activo: Boolean) -> Unit) {
    var username by remember { mutableStateOf(usuario?.username ?: "") }; var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }; var pin by remember { mutableStateOf("") }; var rol by remember { mutableStateOf(usuario?.rol ?: "seller") }; var almacenId by remember { mutableStateOf(usuario?.almacen_id ?: "1") }; var androidId by remember { mutableStateOf(usuario?.android_id ?: "") }; var activo by remember { mutableStateOf(usuario?.activo ?: true) }; var rolMenuAbierto by remember { mutableStateOf(false) }; var localMenuAbierto by remember { mutableStateOf(false) }
    val esEdicion = usuario != null; val pinValido = pin.length in 4..6 && pin.all { it.isDigit() }; val formularioValido = username.isNotBlank() && (esEdicion || pinValido)
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (esEdicion) "Editar usuario" else "Nuevo usuario") }, text = { Column {
        OutlinedTextField(username, { username = it }, label = { Text("Usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp))
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") }, singleLine = true, modifier = Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp))
        if (!esEdicion) { OutlinedTextField(pin, { if (it.length <= 6) pin = it.filter { c -> c.isDigit() } }, label = { Text("PIN (4 a 6 dígitos)") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword), modifier = Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp)) }
        ExposedDropdownMenuBox(expanded = rolMenuAbierto, onExpandedChange = { rolMenuAbierto = it }) { OutlinedTextField(rol, {}, readOnly = true, label = { Text("Rol") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(rolMenuAbierto) }, modifier = Modifier.menuAnchor().fillMaxWidth()); ExposedDropdownMenu(expanded = rolMenuAbierto, onDismissRequest = { rolMenuAbierto = false }) { ROLES.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { rol = it; rolMenuAbierto = false }) } } }
        if (rol == "seller") { Spacer(Modifier.height(8.dp)); ExposedDropdownMenuBox(expanded = localMenuAbierto, onExpandedChange = { localMenuAbierto = it }) { val sel = locales.find { it.id.toString() == almacenId }; OutlinedTextField(sel?.nombre ?: "Seleccionar local", {}, readOnly = true, label = { Text("Local asignado") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(localMenuAbierto) }, modifier = Modifier.menuAnchor().fillMaxWidth()); ExposedDropdownMenu(expanded = localMenuAbierto, onDismissRequest = { localMenuAbierto = false }) { if (locales.isEmpty()) DropdownMenuItem(text = { Text("No hay locales") }, onClick = { localMenuAbierto = false }) else locales.forEach { l -> DropdownMenuItem(text = { Text("🏪 ${l.nombre}") }, onClick = { almacenId = l.id.toString(); localMenuAbierto = false }) } } } }
        Spacer(Modifier.height(8.dp)); OutlinedTextField(androidId, { androidId = it.trim() }, label = { Text("Android ID") }, supportingText = { Text("Dispositivo autorizado para este usuario") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (esEdicion) { Spacer(Modifier.height(10.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Switch(checked = activo, onCheckedChange = { activo = it }); Spacer(Modifier.width(8.dp)); Text(if (activo) "Activo" else "Inactivo") } }
    } }, confirmButton = { TextButton(enabled = formularioValido && !isSaving, onClick = { onGuardar(username.trim(), nombre.trim(), pin, rol, almacenId, androidId.trim(), activo) }) { Text(if (isSaving) "Guardando..." else "Guardar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
