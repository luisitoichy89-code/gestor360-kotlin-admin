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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Ticket
import org.luisito.admin360.data.models.TicketMensaje
import org.luisito.admin360.data.repository.TicketRepository

data class TicketAdminUiState(val isLoading: Boolean = false, val tickets: List<Ticket> = emptyList(), val mensajes: List<TicketMensaje> = emptyList(), val ticketSeleccionado: Ticket? = null, val error: String? = null)

class TicketAdminViewModel(private val repo: TicketRepository = TicketRepository()) : ViewModel() {
    private val _s = MutableStateFlow(TicketAdminUiState()); val uiState: StateFlow<TicketAdminUiState> = _s.asStateFlow()
    fun cargar() { viewModelScope.launch { _s.value = _s.value.copy(isLoading = true); repo.getTodosTickets().onSuccess { _s.value = _s.value.copy(isLoading = false, tickets = it) }.onFailure { _s.value = _s.value.copy(isLoading = false, error = it.message) } } }
    fun abrirTicket(t: Ticket) { _s.value = _s.value.copy(ticketSeleccionado = t); viewModelScope.launch { repo.getMensajes(t.id!!).onSuccess { _s.value = _s.value.copy(mensajes = it) } } }
    fun responder(mensaje: String) { val t = _s.value.ticketSeleccionado ?: return; viewModelScope.launch { repo.responderTicket("admin", t.id!!, mensaje).onSuccess { abrirTicket(t) } } }
    fun cambiarEstado(ticketId: Long, estado: String) { viewModelScope.launch { repo.cambiarEstado(ticketId, estado).onSuccess { cargar() } } }
    fun cerrar() { _s.value = _s.value.copy(ticketSeleccionado = null, mensajes = emptyList()) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsAdminScreen(onBack: () -> Unit, vm: TicketAdminViewModel = viewModel()) {
    val s by vm.uiState.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }

    if (s.ticketSeleccionado != null) {
        var nuevoMensaje by remember { mutableStateOf("") }
        var showEstadoMenu by remember { mutableStateOf(false) }
        Scaffold(topBar = { TopAppBar(title = { Text("Ticket #${s.ticketSeleccionado!!.id}"), navigationIcon = { IconButton(onClick = { vm.cerrar() }) { Icon(Icons.Default.ArrowBack, "Volver") } }, actions = { IconButton(onClick = { showEstadoMenu = true }) { Icon(Icons.Default.MoreVert, "Estado") } }) }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) { items(s.mensajes) { m -> val esAdmin = m.autor == "admin"; Column(Modifier.fillMaxWidth(), horizontalAlignment = if (!esAdmin) Alignment.Start else Alignment.End) { Surface(color = if (!esAdmin) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) { Column(Modifier.padding(12.dp)) { Text(m.autor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall); Text(m.mensaje); Text(m.created_at?.take(16)?.replace("T", " ") ?: "", style = MaterialTheme.typography.labelSmall) } } } } }
                Row(Modifier.padding(8.dp)) { OutlinedTextField(nuevoMensaje, { nuevoMensaje = it }, label = { Text("Mensaje") }, modifier = Modifier.weight(1f), singleLine = true); IconButton(onClick = { if (nuevoMensaje.isNotBlank()) { vm.responder(nuevoMensaje); nuevoMensaje = "" } }) { Icon(Icons.Default.Send, "Enviar") } }
            }
            if (showEstadoMenu) { AlertDialog(onDismissRequest = { showEstadoMenu = false }, title = { Text("Cambiar estado") }, text = { Column { listOf("pendiente", "en_revision", "resuelto").forEach { e -> TextButton(onClick = { vm.cambiarEstado(s.ticketSeleccionado!!.id!!, e); showEstadoMenu = false }, Modifier.fillMaxWidth()) { Text(e.replace("_", " ")) } } } }, confirmButton = {}, dismissButton = { TextButton(onClick = { showEstadoMenu = false }) { Text("Cancelar") } }) }
        }
    } else {
        Scaffold(topBar = { TopAppBar(title = { Text("Tickets de soporte") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver") } }) }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                when { s.isLoading -> CircularProgressIndicator(); s.error != null -> Text(s.error!!, color = MaterialTheme.colorScheme.error); s.tickets.isEmpty() -> Text("No hay tickets"); else -> LazyColumn { items(s.tickets) { t -> TicketItemAdmin(t) { vm.abrirTicket(t) } } } }
            }
        }
    }
}

@Composable
private fun TicketItemAdmin(t: Ticket, onClick: () -> Unit) {
    val color = when(t.estado) { "pendiente" -> MaterialTheme.colorScheme.error; "en_revision" -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.primary }
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = onClick) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("#${t.id} - ${t.usuario_nombre}", fontWeight = FontWeight.Bold); Text(t.updated_at?.take(16)?.replace("T", " ") ?: ""); if (!t.telefono_contacto.isNullOrBlank()) Text("📞 ${t.telefono_contacto}") }
            Surface(color = color, shape = MaterialTheme.shapes.small) { Text(t.estado.replace("_", " "), Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary) }
        }
    }
}
