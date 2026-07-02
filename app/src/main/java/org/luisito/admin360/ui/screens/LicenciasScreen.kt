package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.models.getDiasRestantes
import org.luisito.admin360.ui.components.ConfirmarEliminarDialog
import org.luisito.admin360.ui.components.EstadoCargando
import org.luisito.admin360.ui.components.EstadoChip
import org.luisito.admin360.ui.components.EstadoError
import org.luisito.admin360.ui.components.EstadoVacio
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenciasScreen(
    clienteId: String,
    viewModel: LicenciaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }
    var licenciaAEliminar by remember { mutableStateOf<Licencia?>(null) }

    LaunchedEffect(clienteId) {
        viewModel.loadLicencias(clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Licencias") },
                actions = {
                    IconButton(onClick = { viewModel.refrescar() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarFormulario = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Activar licencia") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> EstadoCargando()
                uiState.error != null -> EstadoError(uiState.error ?: "Error desconocido") { viewModel.refrescar() }
                uiState.licencias.isEmpty() -> EstadoVacio("Este cliente no tiene licencias activadas")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.licencias, key = { it.id ?: it.device_id }) { licencia ->
                        LicenciaCard(
                            licencia = licencia,
                            onRenovar = { dias -> viewModel.renewLicense(clienteId, dias) },
                            onToggleActivo = { viewModel.toggleActivo(licencia) },
                            onEliminar = { licenciaAEliminar = licencia }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (mostrarFormulario) {
        ActivarLicenciaDialog(
            isSaving = uiState.isSaving,
            onDismiss = { mostrarFormulario = false },
            onActivar = { deviceId, dias ->
                viewModel.activateLicense(clienteId, deviceId, dias)
                mostrarFormulario = false
            }
        )
    }

    licenciaAEliminar?.let { licencia ->
        ConfirmarEliminarDialog(
            nombre = licencia.device_id,
            onConfirm = {
                licencia.id?.let { viewModel.deleteLicense(it) }
                licenciaAEliminar = null
            },
            onDismiss = { licenciaAEliminar = null }
        )
    }
}

@Composable
private fun LicenciaCard(
    licencia: Licencia,
    onRenovar: (Int) -> Unit,
    onToggleActivo: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }
    var menuRenovarAbierto by remember { mutableStateOf(false) }
    val diasRestantes = licencia.getDiasRestantes()

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = if (diasRestantes >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(licencia.device_id, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = when {
                        diasRestantes < 0 -> "Vencida"
                        diasRestantes == 0L -> "Vence hoy"
                        else -> "Vence en $diasRestantes días (${licencia.expiracion})"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (diasRestantes <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                EstadoChip(activo = licencia.activo)
            }
            Box {
                IconButton(onClick = { menuRenovarAbierto = true }) {
                    Icon(Icons.Default.Update, contentDescription = "Renovar")
                }
                DropdownMenu(expanded = menuRenovarAbierto, onDismissRequest = { menuRenovarAbierto = false }) {
                    listOf(30, 90, 180, 365).forEach { dias ->
                        DropdownMenuItem(
                            text = { Text("Renovar $dias días") },
                            onClick = { menuRenovarAbierto = false; onRenovar(dias) }
                        )
                    }
                }
            }
            Box {
                IconButton(onClick = { menuAbierto = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    DropdownMenuItem(
                        text = { Text(if (licencia.activo) "Desactivar" else "Activar") },
                        onClick = { menuAbierto = false; onToggleActivo() }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = { menuAbierto = false; onEliminar() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivarLicenciaDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onActivar: (deviceId: String, dias: Int) -> Unit
) {
    var deviceId by remember { mutableStateOf("") }
    var diasTexto by remember { mutableStateOf("30") }
    val dias = diasTexto.toIntOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Activar licencia") },
        text = {
            Column {
                OutlinedTextField(
                    value = deviceId,
                    onValueChange = { deviceId = it },
                    label = { Text("Device ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = diasTexto,
                    onValueChange = { diasTexto = it.filter { c -> c.isDigit() } },
                    label = { Text("Días de vigencia") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = deviceId.isNotBlank() && dias != null && dias > 0 && !isSaving,
                onClick = { onActivar(deviceId.trim(), dias ?: 30) }
            ) {
                Text(if (isSaving) "Activando..." else "Activar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
