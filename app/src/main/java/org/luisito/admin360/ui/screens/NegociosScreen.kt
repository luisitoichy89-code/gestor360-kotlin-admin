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
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.ui.viewmodels.NegocioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegociosScreen(
    onBack: () -> Unit,
    onNegocioSeleccionado: (String) -> Unit,
    viewModel: NegocioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteNegocioId by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editNegocioId by remember { mutableStateOf<String?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editActivo by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { viewModel.loadNegocios() }

    val filteredNegocios = uiState.negocios
        .filter { it.nombre_negocio.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true) }
        .sortedByDescending { it.created_at }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏢 Negocios") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", color = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }) { Icon(Icons.Default.Add, "Agregar") } }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Buscador anclado arriba
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("🔍 Buscar negocio...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp))
            } else if (uiState.error != null) {
                Text(uiState.error ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(filteredNegocios, key = { it.id }) { negocio ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(negocio.nombre_negocio, style = MaterialTheme.typography.titleMedium)
                                    Text("ID: ${negocio.id.take(8)}... | ${if (negocio.activo) "🟢 Activo" else "🔴 Inactivo"}", style = MaterialTheme.typography.bodySmall)
                                }
                                Row {
                                    IconButton(onClick = { onNegocioSeleccionado(negocio.id) }) { Text("📂") }
                                    IconButton(onClick = {
                                        editNegocioId = negocio.id; editNombre = negocio.nombre_negocio; editActivo = negocio.activo; showEditDialog = true
                                    }) { Icon(Icons.Default.Edit, "Editar") }
                                    IconButton(onClick = { deleteNegocioId = negocio.id; showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showDeleteDialog && deleteNegocioId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("⚠️ Eliminar negocio") },
            text = { Text("¿Estás seguro? Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteNegocio(deleteNegocioId!!); showDeleteDialog = false; deleteNegocioId = null }) { Text("Eliminar", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false; deleteNegocioId = null }) { Text("Cancelar") } }
        )
    }

    if (showEditDialog && editNegocioId != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("✏️ Editar negocio") },
            text = { OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { viewModel.updateNegocio(editNegocioId!!, editNombre, editActivo); showEditDialog = false; editNegocioId = null }) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { showEditDialog = false; editNegocioId = null }) { Text("Cancelar") } }
        )
    }

    if (showDialog) {
        var newNombre by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("🏢 Nuevo negocio") },
            text = { OutlinedTextField(value = newNombre, onValueChange = { newNombre = it }, label = { Text("Nombre del negocio") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { if (newNombre.isNotEmpty()) { viewModel.createNegocio(newNombre); showDialog = false } }) { Text("Crear") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }
}
