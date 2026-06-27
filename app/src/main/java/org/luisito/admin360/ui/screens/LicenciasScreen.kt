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
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenciasScreen(
    clienteId: String,
    onBack: () -> Unit,
    viewModel: LicenciaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteLicenciaId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadLicencias(clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📜 Licencias") },
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
                Text(text = uiState.error ?: "Error", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.licencias) { licencia ->
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
                                        text = "Device: ${licencia.device_id}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Expira: ${licencia.expiracion ?: "N/A"} | ${if (licencia.activo) "🟢 Activa" else "🔴 Inactiva"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            deleteLicenciaId = licencia.id
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

    if (showDeleteDialog && deleteLicenciaId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("⚠️ Eliminar licencia") },
            text = { Text("¿Estás seguro?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLicense(deleteLicenciaId!!, clienteId)
                        showDeleteDialog = false
                        deleteLicenciaId = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; deleteLicenciaId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDialog) {
        var newDeviceId by remember { mutableStateOf("") }
        var newDias by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("📜 Nueva licencia") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDeviceId,
                        onValueChange = { newDeviceId = it },
                        label = { Text("Device ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDias,
                        onValueChange = { newDias = it },
                        label = { Text("Días de validez") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newDeviceId.isNotEmpty() && newDias.isNotEmpty()) {
                            viewModel.activateLicense(clienteId, newDeviceId, newDias.toIntOrNull() ?: 30)
                            showDialog = false
                        }
                    }
                ) {
                    Text("Activar")
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
