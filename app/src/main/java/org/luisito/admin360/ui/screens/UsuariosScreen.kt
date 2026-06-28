package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.ui.viewmodels.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    clienteId: Int,
    onBack: () -> Unit,
    viewModel: UsuarioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteUsuarioId by remember { mutableStateOf<Int?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editUsuarioId by remember { mutableStateOf<Int?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetUsuarioId by remember { mutableStateOf<Int?>(null) }

    // Campos para nuevo usuario
    var newUsername by remember { mutableStateOf("") }
    var newNombre by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newRol by remember { mutableStateOf("seller") }
    var newAlmacenId by remember { mutableStateOf("") }

    // Campos para editar
    var editUsername by remember { mutableStateOf("") }
    var editNombre by remember { mutableStateOf("") }
    var editRol by remember { mutableStateOf("seller") }
    var editAlmacenId by remember { mutableStateOf("") }
    var editActivo by remember { mutableStateOf(true) }

    var showRolMenu by remember { mutableStateOf(false) }
    var showEditRolMenu by remember { mutableStateOf(false) }

    val roles = listOf("admin_almacen", "seller")

    LaunchedEffect(Unit) {
        viewModel.loadUsuarios(clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👥 Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.error != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.error ?: "Error", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.clearError() }) {
                        Text("Reintentar")
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.usuarios) { usuario ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${usuario.nombre ?: usuario.username}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Usuario: ${usuario.username} | Rol: ${usuario.rol} | ${if (usuario.activo) "🟢 Activo" else "🔴 Inactivo"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Local ID: ${usuario.almacen_id} | Auth ID: ${usuario.auth_id?.toString()?.take(8) ?: "Sin auth"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            resetUsuarioId = usuario.id
                                            showResetDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Resetear password")
                                    }
                                    IconButton(
                                        onClick = {
                                            editUsuarioId = usuario.id
                                            editUsername = usuario.username
                                            editNombre = usuario.nombre ?: ""
                                            editRol = usuario.rol
                                            editAlmacenId = usuario.almacen_id.toString()
                                            editActivo = usuario.activo
                                            showEditDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(
                                        onClick = {
                                            deleteUsuarioId = usuario.id
                                            showDeleteDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Crear usuario
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("👤 Nuevo usuario") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newNombre,
                        onValueChange = { newNombre = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAlmacenId,
                        onValueChange = { newAlmacenId = it },
                        label = { Text("ID del local") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column {
                        Text("Rol: $newRol", style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { showRolMenu = true }) {
                            Text("Seleccionar rol")
                        }
                        DropdownMenu(
                            expanded = showRolMenu,
                            onDismissRequest = { showRolMenu = false }
                        ) {
                            roles.forEach { rol ->
                                DropdownMenuItem(
                                    text = { Text(rol) },
                                    onClick = {
                                        newRol = rol
                                        showRolMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newUsername.isNotEmpty() && newPassword.isNotEmpty() && newAlmacenId.isNotEmpty()) {
                            viewModel.createUsuario(
                                username = newUsername,
                                nombre = newNombre,
                                password = newPassword,
                                rol = newRol,
                                clienteId = clienteId,
                                almacenId = newAlmacenId.toIntOrNull() ?: 0
                            )
                            showDialog = false
                            newUsername = ""
                            newNombre = ""
                            newPassword = ""
                            newAlmacenId = ""
                            newRol = "seller"
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Eliminar usuario
    if (showDeleteDialog && deleteUsuarioId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("⚠️ Eliminar usuario") },
            text = { Text("¿Estás seguro de eliminar este usuario?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUsuario(deleteUsuarioId!!, clienteId)
                        showDeleteDialog = false
                        deleteUsuarioId = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; deleteUsuarioId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Resetear contraseña
    if (showResetDialog && resetUsuarioId != null) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("🔑 Resetear contraseña") },
            text = { Text("¿Restablecer contraseña a '123456'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetPassword(resetUsuarioId!!, clienteId)
                        showResetDialog = false
                        resetUsuarioId = null
                    }
                ) {
                    Text("Resetear", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false; resetUsuarioId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog: Editar usuario
    if (showEditDialog && editUsuarioId != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("✏️ Editar usuario") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editNombre,
                        onValueChange = { editNombre = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAlmacenId,
                        onValueChange = { editAlmacenId = it },
                        label = { Text("ID del local") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column {
                        Text("Rol: $editRol", style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { showEditRolMenu = true }) {
                            Text("Seleccionar rol")
                        }
                        DropdownMenu(
                            expanded = showEditRolMenu,
                            onDismissRequest = { showEditRolMenu = false }
                        ) {
                            roles.forEach { rol ->
                                DropdownMenuItem(
                                    text = { Text(rol) },
                                    onClick = {
                                        editRol = rol
                                        showEditRolMenu = false
                                    }
                                )
                            }
                        }
                    }
                    Button(
                        onClick = { editActivo = !editActivo },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (editActivo) "🟢 Activo" else "🔴 Inactivo")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateUsuario(
                            id = editUsuarioId!!,
                            username = editUsername,
                            nombre = editNombre,
                            rol = editRol,
                            almacenId = editAlmacenId.toIntOrNull() ?: 0,
                            activo = editActivo,
                            clienteId = clienteId
                        )
                        showEditDialog = false
                        editUsuarioId = null
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; editUsuarioId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
