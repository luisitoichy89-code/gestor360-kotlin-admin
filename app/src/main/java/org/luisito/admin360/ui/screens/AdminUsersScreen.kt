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
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteUserId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(clienteId) {
        viewModel.loadUsers(clienteId)
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
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.error != null) {
                Text(text = uiState.error ?: "Error", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.users) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${user.username} (${user.rol})",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Local: ${user.almacen_id} | ${if (user.activo) "🟢 Activo" else "🔴 Inactivo"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (user.device_id_pendiente != null && !user.device_approved) {
                                        Text(
                                            text = "📱 ID pendiente: ${user.device_id_pendiente?.take(12)}...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Yellow
                                        )
                                        Button(
                                            onClick = {
                                                viewModel.approveUser(user.id)
                                            },
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Text("✅ Aceptar")
                                        }
                                    }
                                    if (user.device_approved) {
                                        Text(
                                            text = "✅ Dispositivo aprobado",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Green
                                        )
                                    }
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            // TODO: Editar usuario
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(
                                        onClick = {
                                            deleteUserId = user.id
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

    if (showDeleteDialog && deleteUserId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("⚠️ Eliminar usuario") },
            text = { Text("¿Estás seguro de que quieres eliminar este usuario?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteUser(deleteUserId!!, clienteId)
                        showDeleteDialog = false
                        deleteUserId = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; deleteUserId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDialog) {
        var newUsername by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var newNombre by remember { mutableStateOf("") }
        var newRol by remember { mutableStateOf("seller") }
        var newAlmacenId by remember { mutableStateOf("") }

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
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newNombre,
                        onValueChange = { newNombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAlmacenId,
                        onValueChange = { newAlmacenId = it },
                        label = { Text("Local ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newUsername.isNotEmpty() && newPassword.isNotEmpty()) {
                            viewModel.createUser(clienteId, newUsername, newPassword, newNombre, newRol, newAlmacenId)
                            showDialog = false
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
}
