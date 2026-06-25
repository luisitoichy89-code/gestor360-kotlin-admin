package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import org.luisito.admin360.ui.viewmodels.AdminUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendientesScreen(
    clienteId: String,
    onBack: () -> Unit,
    viewModel: AdminUserViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(clienteId) { viewModel.loadUsers(clienteId) }

    val pendientes = uiState.users
        .filter { it.reset_requested && (it.username.contains(searchQuery, ignoreCase = true) || (it.nombre?.contains(searchQuery, ignoreCase = true) == true)) }
        .sortedByDescending { it.created_at }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🔑 Resets Pendientes") }, navigationIcon = { IconButton(onClick = onBack) { Text("←", color = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White))
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text("🔍 Buscar usuario...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp))
            if (uiState.isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp)) }
            else if (pendientes.isEmpty()) { Text("✅ No hay resets pendientes", modifier = Modifier.padding(24.dp), color = Color.Green) }
            else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    items(pendientes, key = { it.id }) { user ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${user.username} (${user.rol})", style = MaterialTheme.typography.titleMedium)
                                    Text("Local: ${user.almacen_id}", style = MaterialTheme.typography.bodySmall)
                                    Text("🔑 Reset solicitado", style = MaterialTheme.typography.bodySmall, color = Color.Yellow)
                                }
                                Button(onClick = { viewModel.confirmPasswordReset(user.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("✅ Confirmar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
