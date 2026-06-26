package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.luisito.admin360.data.model.Negocio
import org.luisito.admin360.viewmodel.NegocioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegociosScreen(
    viewModel: NegocioViewModel,
    onNavigateBack: () -> Unit
) {
    val negocios by viewModel.negocios.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editNegocioId by remember { mutableStateOf<String?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editActivo by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏢 Negocios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(negocios) { negocio ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        editNegocioId = negocio.id
                        editNombre = negocio.nombre_negocio
                        editActivo = negocio.activo
                        showEditDialog = true
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(negocio.nombre_negocio, style = MaterialTheme.typography.titleMedium)
                            Text("ID: ${negocio.id}", style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (negocio.activo) "🟢" else "🔴")
                            IconButton(onClick = { viewModel.deleteNegocio(negocio.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para editar
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false; editNegocioId = null },
            title = { Text("✏️ Editar negocio") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editNombre,
                        onValueChange = { editNombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = editActivo,
                            onCheckedChange = { editActivo = it }
                        )
                        Text("Activo")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val negocio = Negocio(
                            id = editNegocioId!!,
                            nombre_negocio = editNombre,
                            activo = editActivo
                        )
                        viewModel.updateNegocio(negocio)
                        showEditDialog = false
                        editNegocioId = null
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false; editNegocioId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog para crear
    if (showDialog) {
        var newNombre by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("🏢 Nuevo negocio") },
            text = {
                OutlinedTextField(
                    value = newNombre,
                    onValueChange = { newNombre = it },
                    label = { Text("Nombre del negocio") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newNombre.isNotEmpty()) {
                            val nuevoNegocio = Negocio(
                                id = "",
                                nombre_negocio = newNombre,
                                activo = true
                            )
                            viewModel.addNegocio(nuevoNegocio)
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
