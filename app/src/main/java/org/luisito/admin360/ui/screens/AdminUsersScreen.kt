package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.ui.viewmodels.AdminUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    clienteId: String,
    onBack: () -> Unit,
    viewModel: AdminUserViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteUserId by remember { mutableStateOf<Int?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editUserId by remember { mutableStateOf<Int?>(null) }
    var editUsername by remember { mutableStateOf("") }
    var editNombre by remember { mutableStateOf("") }
    var editRol by remember { mutableStateOf("seller") }
    var editAlmacenId by remember { mutableStateOf("") }
    var editActivo by remember { mutableStateOf(true) }
    var editDeviceId by remember { mutableStateOf("") }

    LaunchedEffect(clienteId) { viewModel.loadUsers(clienteId) }

    val filteredUsers = uiState.users
        .filter { it.username.contains(searchQuery, ignoreCase = true) || (it.nombre?.contains(searchQuery, ignoreCase = true) == true) || it.almacen_id.contains(searchQuery, ignoreCase = true) }
        .sortedByDescending { it.created_at }

    Scaffold(
        topBar = {
            TopAppBar(                title = { Text("👥 Usuarios") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", color = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, "Agregar") } }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("🔍 Buscar usuario, nombre o local...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp))
            } else if (uiState.error != null) {
                Text(uiState.error ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filteredUsers, key = { it.id }) { user ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${user.username} (${user.rol})", style = MaterialTheme.typography.titleMedium)
                                    Text("Local: ${user.almacen_id} | ${if (user.activo) "🟢 Activo" else "🔴 Inactivo"}", style = MaterialTheme.typography.bodySmall)
                                    if (!user.device_id.isNullOrEmpty()) {
                                        Text("📱 Device: ${user.device_id?.take(12)}...", style = MaterialTheme.typography.bodySmall, color = Color.Cyan)
                                    }
                                    if (user.reset_requested) {
                                        Text("🔑 Reset solicitado", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
                                        Button(onClick = { viewModel.confirmPasswordReset(user.id) }, modifier = Modifier.padding(top = 4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("✅ Confirmar Reset") }
                                    }
                                }
                                Row {
                                    IconButton(onClick = {
                                        editUserId = user.id; editUsername = user.username; editNombre = user.nombre ?: ""; editRol = user.rol
                                        editAlmacenId = user.almacen_id; editActivo = user.activo; editDeviceId = user.device_id ?: ""; showEditDialog = true
                                    }) { Icon(Icons.Default.Edit, "Editar") }
                                    IconButton(onClick = { deleteUserId = user.id; showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
        }    }

    if (showDeleteDialog && deleteUserId != null) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("⚠️ Eliminar usuario") }, text = { Text("¿Estás seguro?") },
            confirmButton = { TextButton(onClick = { viewModel.deleteUser(deleteUserId!!, clienteId); showDeleteDialog = false; deleteUserId = null }) { Text("Eliminar", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false; deleteUserId = null }) { Text("Cancelar") } })
    }

    if (showEditDialog && editUserId != null) {
        AlertDialog(onDismissRequest = { showEditDialog = false }, title = { Text("✏️ Editar usuario") },
            text = { Column {
                OutlinedTextField(value = editUsername, onValueChange = { editUsername = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(), enabled = false)
                OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = editAlmacenId, onValueChange = { editAlmacenId = it }, label = { Text("Local ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = editDeviceId, onValueChange = { editDeviceId = it }, label = { Text("Android ID dispositivo") }, modifier = Modifier.fillMaxWidth())
            }},
            confirmButton = { TextButton(onClick = { viewModel.updateUser(editUserId!!, editUsername, editNombre, editRol, editAlmacenId, editActivo, editDeviceId); showEditDialog = false; editUserId = null }) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { showEditDialog = false; editUserId = null }) { Text("Cancelar") } })
    }

    if (showDialog) {
        var newUsername by remember { mutableStateOf("") }; var newPassword by remember { mutableStateOf("") }
        var newNombre by remember { mutableStateOf("") }; var newRol by remember { mutableStateOf("seller") }
        var newAlmacenId by remember { mutableStateOf("") }; var newDeviceId by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showDialog = false }, title = { Text("👤 Nuevo usuario") },
            text = { Column {
                OutlinedTextField(value = newUsername, onValueChange = { newUsername = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = newNombre, onValueChange = { newNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = newAlmacenId, onValueChange = { newAlmacenId = it }, label = { Text("Local ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = newDeviceId, onValueChange = { newDeviceId = it }, label = { Text("Android ID dispositivo") }, placeholder = { Text("G360-XXXXXXXXXXXX") }, modifier = Modifier.fillMaxWidth())
            }},
            confirmButton = { TextButton(onClick = { if (newUsername.isNotEmpty() && newPassword.isNotEmpty()) { viewModel.createUser(clienteId, newUsername, newPassword, newNombre, newRol, newAlmacenId, newDeviceId); showDialog = false } }) { Text("Crear") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } })
    }
}
