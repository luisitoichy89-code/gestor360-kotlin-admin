package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.models.getDiasRestantes
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

@Composable
fun LicenciasScreen(
    clienteId: String,
    viewModel: LicenciaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLicencias(clienteId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error)
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.licencias) { licencia: Licencia ->

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text("Device: ${licencia.device_id}")
                                Text("Activo: ${licencia.activo}")

                                Text(
                                    "Días restantes: ${licencia.getDiasRestantes()}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
