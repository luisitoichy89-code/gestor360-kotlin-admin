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
import androidx.compose.material.icons.filled.Search
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
import org.luisito.admin360.ui.viewmodels.LocalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalesScreen(
    clienteId: String,
    onBack: () -> Unit,
    viewModel: LocalViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteLocalId by remember { mutableStateOf<Int?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editLocalId by remember { mutableStateOf<Int?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editActivo by remember { mutableStateOf(true) }

    LaunchedEffect(clienteId) { viewModel.loadLocales(clienteId) }

    val filteredLocales = uiState.locales
        .filter { it.nombre.contains(searchQuery, ignoreCase = true) || it.id.toString().contains(searchQuery) }
        .sortedByDescending { it.created_at }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏪 Locales") },
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
                placeholder = { Text("🔍 Buscar local...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp))
            } else if (uiState.error != null) {
                Text(uiState.error ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filteredLocales, key = { it.id }) { local ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {                                Column(modifier = Modifier.weight(1f)) {
                                    Text(local.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text("ID: ${local.id} | ${if (local.activo) "🟢 Activo" else "🔴 Inactivo"}", style = MaterialTheme.typography.bodySmall)
                                }
                                Row {
                                    IconButton(onClick = {
                                        editLocalId = local.id; editNombre = local.nombre; editActivo = local.activo; showEditDialog = true
                                    }) { Icon(Icons.Default.Edit, "Editar") }
                                    IconButton(onClick = { deleteLocalId = local.id; showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && deleteLocalId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("⚠️ Eliminar local") },
            text = { Text("¿Estás seguro? Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteLocal(deleteLocalId!!, clienteId); showDeleteDialog = false; deleteLocalId = null }) { Text("Eliminar", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false; deleteLocalId = null }) { Text("Cancelar") } }
        )
    }

    if (showEditDialog && editLocalId != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("✏️ Editar local") },
            text = { OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { viewModel.updateLocal(editLocalId!!, editNombre, editActivo, clienteId); showEditDialog = false; editLocalId = null }) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { showEditDialog = false; editLocalId = null }) { Text("Cancelar") } }
        )
    }

    if (showDialog) {
        var newNombre by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("🏪 Nuevo local") },
            text = { OutlinedTextField(value = newNombre, onValueChange = { newNombre = it }, label = { Text("Nombre del local") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { if (newNombre.isNotEmpty()) { viewModel.createLocal(clienteId, newNombre); showDialog = false } }) { Text("Crear") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }
}
