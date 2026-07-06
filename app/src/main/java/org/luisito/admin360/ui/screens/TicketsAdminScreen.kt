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

data class TicketAdminUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
    val mensajes: List<TicketMensaje> = emptyList(),
    val ticketSeleccionado: Ticket? = null,
    val error: String? = null
)

class TicketAdminViewModel(private val repo: TicketRepository = TicketRepository()) : ViewModel() {
    private val _s = MutableStateFlow(TicketAdminUiState())
    val uiState: StateFlow<TicketAdminUiState> = _s.asStateFlow()

    fun cargar() { viewModelScope.launch { _s.value = _s.value.copy(isLoading = true); repo.getTodosTickets().onSuccess { _s.value = _s.value.copy(isLoading = false, tickets = it) }.onFailure { _s.value = _s.value.copy(isLoading = false, error = it.message) } } }
    fun abrirTicket(t: Ticket) { _s.value = _s.value.copy(ticketSeleccionado = t, mensajes = emptyList()); viewModelScope.launch { repo.getMensajes(t.id!!).onSuccess { _s.value = _s.value.copy(mensajes = it) } } }
    fun responder(mensaje: String) { val t = _s.value.ticketSeleccionado ?: return; viewModelScope.launch { _s.value = _s.value.copy(isSending = true); repo.responderTicket(t.id!!, mensaje).onSuccess { abrirTicket(t) }.onFailure { _s.value = _s.value.copy(error = it.message) }; _s.value = _s.value.copy(isSending = false) } }
    fun cambiarEstado(ticketId: Long, estado: String) { viewModelScope.launch { repo.cambiarEstado(ticketId, estado).onSuccess { cargar() } } }
    fun cerrar() { _s.value = _s.value.copy(ticketSeleccionado = null, mensajes = emptyList()) }
}

@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsAdminScreen(onBack: () -> Unit, vm: TicketAdminViewModel = viewModel()) {
    val s by vm.uiState.collectAsState()
    LaunchedEffect(Unit) { vm.cargar() }
    if (s.ticketSeleccionado != null) TicketChatView(s, vm, { vm.cerrar() }) else TicketListView(s, vm, onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketListView(state: TicketAdminUiState, vm: TicketAdminViewModel, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Tickets de soporte") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }, actions = { IconButton(onClick = vm::cargar) { Icon(Icons.Default.Refresh, null) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            when { state.isLoading -> CircularProgressIndicator(); state.error != null -> Text(state.error!!, color = MaterialTheme.colorScheme.error); state.tickets.isEmpty() -> Text("No hay tickets todavía"); else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(state.tickets) { t -> TicketCard(t) { vm.abrirTicket(t) } } } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketCard(t: Ticket, onClick: () -> Unit) {
    val color = when (t.estado) { "pendiente" -> MaterialTheme.colorScheme.error; "en_revision" -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.primary }
    ElevatedCard(onClick = onClick) { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("#${t.id}", fontWeight = FontWeight.Bold); Text(t.usuario_nombre ?: "Usuario", style = MaterialTheme.typography.bodySmall) }; AssistChip(onClick = {}, label = { Text(t.estado.replace("_", " ")) }, colors = AssistChipDefaults.assistChipColors(containerColor = color.copy(alpha = 0.15f), labelColor = color)) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketChatView(state: TicketAdminUiState, vm: TicketAdminViewModel, onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text("Ticket #${state.ticketSeleccionado?.id}") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }, actions = { IconButton(onClick = { vm.cambiarEstado(state.ticketSeleccionado!!.id!!, "resuelto") }) { Icon(Icons.Default.Check, null) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { items(state.mensajes) { m -> val isUser = m.autor != "Admin"; Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End) { Surface(color = if (isUser) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) { Column(Modifier.padding(12.dp)) { Text(m.autor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall); Text(m.mensaje); Text(m.created_at?.take(16)?.replace("T", " ") ?: "", style = MaterialTheme.typography.labelSmall) } } } } }
            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) { OutlinedTextField(input, { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("Responder...") }, singleLine = true); IconButton(enabled = input.isNotBlank() && !state.isSending, onClick = { vm.responder(input); input = "" }) { Icon(Icons.Default.Send, null) } }
        }
    }
}
