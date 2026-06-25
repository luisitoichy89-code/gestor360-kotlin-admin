package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlLicenciasScreen(onBack: () -> Unit, viewModel: LicenciaViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showActivateDialog by remember { mutableStateOf(false) }
    var showRenewDialog by remember { mutableStateOf(false) }
    var renewLicenciaId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteLicenciaId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { viewModel.loadLicencias() }

    val filteredLicencias = uiState.licencias
        .filter { it.device_id.contains(searchQuery, ignoreCase = true) || (it.almacen_id?.contains(searchQuery, ignoreCase = true) == true) }
        .sortedByDescending { it.created_at }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🔑 Licencias") }, navigationIcon = { IconButton(onClick = onBack) { Text("←", color = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White))
        },
        floatingActionButton = { FloatingActionButton(onClick = { showActivateDialog = true }) { Icon(Icons.Default.Add, "Activar") } }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text("🔍 Buscar device ID o local...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp))

            if (uiState.isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp)) }
            else if (uiState.error != null) { Text(uiState.error ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) }            else {
                val activas = filteredLicencias.count { it.activo && it.getDiasRestantes() >= 0 }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Card(modifier = Modifier.weight(1f).padding(4.dp)) { Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("${filteredLicencias.size}", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary); Text("Total", style = MaterialTheme.typography.bodySmall) } }
                    Card(modifier = Modifier.weight(1f).padding(4.dp)) { Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("$activas", style = MaterialTheme.typography.headlineMedium, color = Color.Green); Text("🟢 Activas", style = MaterialTheme.typography.bodySmall) } }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(filteredLicencias, key = { it.id }) { licencia ->
                        val dias = licencia.getDiasRestantes(); val estado = licencia.getEstado()
                        val estadoColor = when { dias > 25 -> Color.Green; dias > 4 -> Color.Yellow; dias >= 0 -> Color.Red; else -> Color.Gray }
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("📱 ${licencia.device_id.take(16)}...", style = MaterialTheme.typography.titleSmall, fontFamily = FontFamily.Monospace)
                                    Text("🏪 Local: ${licencia.almacen_id ?: "—"} | 📅 ${licencia.expiracion ?: "Sin fecha"}", style = MaterialTheme.typography.bodySmall)
                                    Text("$estado ($dias días)", style = MaterialTheme.typography.bodySmall, color = estadoColor)
                                }
                                Row {
                                    IconButton(onClick = { renewLicenciaId = licencia.id; showRenewDialog = true }) { Text("🔄") }
                                    IconButton(onClick = { deleteLicenciaId = licencia.id; showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showActivateDialog) {
        var deviceId by remember { mutableStateOf("") }; var almacenId by remember { mutableStateOf("") }; var dias by remember { mutableStateOf("30") }
        AlertDialog(onDismissRequest = { showActivateDialog = false }, title = { Text("🔑 Activar Licencia") },
            text = { Column {
                OutlinedTextField(value = deviceId, onValueChange = { deviceId = it }, label = { Text("Device ID") }, placeholder = { Text("G360-XXXXXXXXXXXX") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = almacenId, onValueChange = { almacenId = it }, label = { Text("Local ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dias, onValueChange = { dias = it }, label = { Text("Días de vigencia") }, modifier = Modifier.fillMaxWidth())
            }},
            confirmButton = { TextButton(onClick = { if (deviceId.isNotEmpty() && almacenId.isNotEmpty()) { viewModel.activateLicencia(deviceId, almacenId, dias.toIntOrNull() ?: 30); showActivateDialog = false } }) { Text("Activar") } },
            dismissButton = { TextButton(onClick = { showActivateDialog = false }) { Text("Cancelar") } })
    }

    if (showRenewDialog && renewLicenciaId != null) {
        var dias by remember { mutableStateOf("30") }
        AlertDialog(onDismissRequest = { showRenewDialog = false }, title = { Text("🔄 Renovar Licencia") },
            text = { OutlinedTextField(value = dias, onValueChange = { dias = it }, label = { Text("Días a renovar") }, modifier = Modifier.fillMaxWidth()) },
            confirmButton = { TextButton(onClick = { viewModel.renewLicencia(renewLicenciaId!!, dias.toIntOrNull() ?: 30); showRenewDialog = false; renewLicenciaId = null }) { Text("Renovar") } },
            dismissButton = { TextButton(onClick = { showRenewDialog = false; renewLicenciaId = null }) { Text("Cancelar") } })
    }

    if (showDeleteDialog && deleteLicenciaId != null) {        AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("⚠️ Eliminar licencia") }, text = { Text("¿Estás seguro?") },
            confirmButton = { TextButton(onClick = { viewModel.deleteLicencia(deleteLicenciaId!!); showDeleteDialog = false; deleteLicenciaId = null }) { Text("Eliminar", color = Color.Red) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false; deleteLicenciaId = null }) { Text("Cancelar") } })
    }
}
