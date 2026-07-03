package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.models.estaBloqueada
import org.luisito.admin360.data.models.getDiasRestantes
import org.luisito.admin360.ui.components.ConfirmarEliminarDialog
import org.luisito.admin360.ui.components.EstadoCargando
import org.luisito.admin360.ui.components.EstadoChip
import org.luisito.admin360.ui.components.EstadoError
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

/**
 * Un negocio = una licencia (la principal, de pago). Admin y vendedores de ese negocio
 * dependen de esta licencia; si vence o se desactiva, todos quedan bloqueados en la app cliente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenciasScreen(
    negocioId: String,
    negocioNombre: String = "",
    onBack: (() -> Unit)? = null,
    viewModel: LicenciaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(negocioId) {
        viewModel.loadLicencia(negocioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Licencia")
                        if (negocioNombre.isNotBlank()) {
                            Text(negocioNombre, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refrescar() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
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
                uiState.licencia == null -> ActivarLicenciaPanel(
                    isSaving = uiState.isSaving,
                    onActivar = { deviceId, dias -> viewModel.activarLicencia(deviceId, dias) }
                )
                else -> LicenciaPanel(
                    licencia = uiState.licencia!!,
                    isSaving = uiState.isSaving,
                    onRenovar = { dias -> viewModel.renovarLicencia(dias) },
                    onToggleActivo = { viewModel.toggleActivo() },
                    onEliminar = { mostrarEliminar = true }
                )
            }
        }
    }

    if (mostrarEliminar) {
        ConfirmarEliminarDialog(
            nombre = "la licencia de este negocio",
            onConfirm = {
                viewModel.eliminarLicencia()
                mostrarEliminar = false
            },
            onDismiss = { mostrarEliminar = false }
        )
    }
}

@Composable
private fun LicenciaPanel(
    licencia: Licencia,
    isSaving: Boolean,
    onRenovar: (Int) -> Unit,
    onToggleActivo: () -> Unit,
    onEliminar: () -> Unit
) {
    val diasRestantes = licencia.getDiasRestantes()
    val bloqueada = licencia.estaBloqueada()

    if (bloqueada) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Negocio bloqueado",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (!licencia.activo) "Licencia desactivada manualmente" else "Licencia vencida",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = if (bloqueada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Licencia principal", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))

            InfoFila("Android ID (admin)", licencia.device_id)
            InfoFila("Vence el", licencia.expiracion)
            InfoFila(
                "Días restantes",
                when {
                    diasRestantes < 0 -> "Vencida hace ${-diasRestantes} días"
                    diasRestantes == 0L -> "Vence hoy"
                    else -> "$diasRestantes días"
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            EstadoChip(activo = !bloqueada)
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
    Text("Renovar por", style = MaterialTheme.typography.labelLarge)
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(30, 90, 180, 365).forEach { dias ->
            OutlinedButton(onClick = { onRenovar(dias) }, enabled = !isSaving) {
                Text("$dias d")
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    RenovarPersonalizado(isSaving = isSaving, onRenovar = onRenovar)

    Spacer(modifier = Modifier.height(24.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = onToggleActivo, enabled = !isSaving) {
            Text(if (licencia.activo) "Desactivar manualmente" else "Reactivar")
        }
        TextButton(
            onClick = onEliminar,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Eliminar licencia")
        }
    }
}

@Composable
private fun InfoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RenovarPersonalizado(isSaving: Boolean, onRenovar: (Int) -> Unit) {
    var diasTexto by remember { mutableStateOf("") }
    val dias = diasTexto.toIntOrNull()

    Text("O por días personalizados", style = MaterialTheme.typography.labelLarge)
    Spacer(modifier = Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = diasTexto,
            onValueChange = { diasTexto = it.filter { c -> c.isDigit() } },
            label = { Text("Días") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            enabled = dias != null && dias > 0 && !isSaving,
            onClick = { dias?.let { onRenovar(it) }; diasTexto = "" }
        ) {
            Text("Renovar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivarLicenciaPanel(
    isSaving: Boolean,
    onActivar: (deviceId: String, dias: Int) -> Unit
) {
    var deviceId by remember { mutableStateOf("") }
    var diasTexto by remember { mutableStateOf("30") }
    val dias = diasTexto.toIntOrNull()

    Text(
        "Este negocio aún no tiene licencia. Actívala con el Android ID del primer admin que se registró.",
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = deviceId,
        onValueChange = { deviceId = it },
        label = { Text("Android ID del admin") },
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
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        enabled = deviceId.isNotBlank() && dias != null && dias > 0 && !isSaving,
        onClick = { onActivar(deviceId.trim(), dias ?: 30) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (isSaving) "Activando..." else "Activar licencia")
    }
}
