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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
    var showEditDialog by remember { mutableStateOf(false) }
    var editUserId by remember { mutableStateOf<Int?>(null) }
    var editUsername by remember { mutableStateOf("") }
    var editNombre by remember { mutableStateOf("") }
    var editRol by remember { mutableStateOf("seller") }
    var editAlmacenId by remember { mutableStateOf("") }
    var editActivo by remember { mutableStateOf(true) }

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
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            editUserId = user.id
                                            editUsername = user.username
                                            editNombre = user.nombre ?: ""
                                            editRol = user.rol
                                            editAlmacenId = user.almacen_id
                                            editActivo = user.activo
                                            showEditDialog = true
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

    // Dialog para eliminar
    if (showDeleteDialog && deleteUserId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("⚠️ Eliminar usuario") },
            text = { Text("¿Estás seguro de que quieres eliminar este usuario? Esta acción no se puede deshacer.") },
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

    // Dialog para editar
    if (showEditDialog && editUserId != null) {
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
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAlmacenId,
                        onValueChange = { editAlmacenId = it },
                        label = { Text("Local ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateUser(editUserId!!, editUsername, editNombre, editRol, editAlmacenId, editActivo)
                        showEditDialog = false
                        editUserId = null
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; editUserId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog para crear
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
