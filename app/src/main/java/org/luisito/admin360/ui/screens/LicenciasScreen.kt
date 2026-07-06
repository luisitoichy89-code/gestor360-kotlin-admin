package org.luisito.admin360.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.models.estaBloqueada
import org.luisito.admin360.data.models.getDiasRestantes
import org.luisito.admin360.ui.components.*
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenciasScreen(negocioId: String, negocioNombre: String = "", onBack: (() -> Unit)? = null, viewModel: LicenciaViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarEliminar by remember { mutableStateOf(false) }
    LaunchedEffect(negocioId) { viewModel.loadLicencia(negocioId) }
    Scaffold(containerColor = Color(0xFF0B0F14), topBar = { TopAppBar(title = { Column { Text("Licencia", fontWeight = FontWeight.Bold); if (negocioNombre.isNotBlank()) Text(negocioNombre, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }, navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }, actions = { IconButton(onClick = { viewModel.refrescar() }) { Icon(Icons.Default.Refresh, null) } }) }) { padding ->
        Column(Modifier.fillMaxSize().background(Color(0xFF0B0F14)).padding(padding).padding(16.dp)) { when { uiState.isLoading -> EstadoCargando(); uiState.error != null -> EstadoError(uiState.error ?: "Error") { viewModel.refrescar() }; uiState.licencia == null -> ActivarLicenciaPanel(uiState.isSaving) { deviceId, dias -> viewModel.activarLicencia(deviceId, dias) }; else -> LicenciaPanel(uiState.licencia!!, uiState.isSaving, { viewModel.renovarLicencia(it) }, { viewModel.toggleActivo() }, { mostrarEliminar = true }) } }
    }
    if (mostrarEliminar) ConfirmarEliminarDialog("la licencia de este negocio", { viewModel.eliminarLicencia(); mostrarEliminar = false }, { mostrarEliminar = false })
}

@Composable
private fun LicenciaPanel(licencia: Licencia, isSaving: Boolean, onRenovar: (Int) -> Unit, onToggleActivo: () -> Unit, onEliminar: () -> Unit) {
    val diasRestantes = licencia.getDiasRestantes(); val bloqueada = licencia.estaBloqueada()
    Surface(color = Color(0xFF111827), shape = RoundedCornerShape(20.dp), tonalElevation = 6.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.VerifiedUser, null, tint = if (bloqueada) MaterialTheme.colorScheme.error else Color(0xFF60A5FA)); Spacer(Modifier.width(10.dp)); Text("Licencia principal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(12.dp)); InfoFila("Android ID", licencia.device_id); InfoFila("Vence", licencia.expiracion); InfoFila("Estado", when { diasRestantes < 0 -> "Vencida"; diasRestantes == 0L -> "Vence hoy"; else -> "$diasRestantes días restantes" }); Spacer(Modifier.height(10.dp)); EstadoChip(activo = !bloqueada); Spacer(Modifier.height(18.dp)); Text("Renovación rápida", fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf(30, 90, 180, 365).forEach { dias -> OutlinedButton(onClick = { onRenovar(dias) }, enabled = !isSaving, shape = RoundedCornerShape(12.dp)) { Text("$dias d") } } }; Spacer(Modifier.height(20.dp)); RenovarPersonalizado(isSaving, onRenovar); Spacer(Modifier.height(20.dp)); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedButton(onClick = onToggleActivo, enabled = !isSaving) { Text(if (licencia.activo) "Desactivar" else "Activar") }; TextButton(onClick = onEliminar, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Icon(Icons.Default.Delete, null); Spacer(Modifier.width(4.dp)); Text("Eliminar") } } }
    }
}

@Composable
private fun ActivarLicenciaPanel(isSaving: Boolean, onActivar: (String, Int) -> Unit) {
    var deviceId by remember { mutableStateOf("") }; var diasTexto by remember { mutableStateOf("30") }; val dias = diasTexto.toIntOrNull()
    Surface(color = Color(0xFF111827), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Activar licencia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.height(12.dp)); OutlinedTextField(deviceId, { deviceId = it }, label = { Text("Android ID") }, modifier = Modifier.fillMaxWidth()); Spacer(Modifier.height(10.dp)); OutlinedTextField(diasTexto, { diasTexto = it.filter(Char::isDigit) }, label = { Text("Días") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)); Spacer(Modifier.height(16.dp)); Button(onClick = { onActivar(deviceId.trim(), dias ?: 30) }, enabled = deviceId.isNotBlank() && dias != null && dias > 0 && !isSaving, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text(if (isSaving) "Activando..." else "Activar") } } }
}

@Composable
private fun InfoFila(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium); Surface(color = Color(0xFF1F2937), shape = RoundedCornerShape(10.dp)) { Text(valor, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface) } }
}

@Composable
private fun RenovarPersonalizado(isSaving: Boolean, onRenovar: (Int) -> Unit) {
    var diasTexto by remember { mutableStateOf("") }; val dias = diasTexto.toIntOrNull()
    Column { Text("Renovación personalizada", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); Surface(color = Color(0xFF111827), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { OutlinedTextField(diasTexto, { diasTexto = it.filter(Char::isDigit) }, label = { Text("Días") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)); Spacer(Modifier.width(10.dp)); Button(onClick = { dias?.let { onRenovar(it) }; diasTexto = "" }, enabled = dias != null && dias > 0 && !isSaving, shape = RoundedCornerShape(12.dp)) { Text("OK") } } } }
}
