package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import org.luisito.admin360.ui.viewmodels.NegocioViewModel
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onMenuClick: () -> Unit,
    onNegocioClick: (String) -> Unit,
    onPendientesClick: () -> Unit,
    negocioViewModel: NegocioViewModel = viewModel(),
    licenciaViewModel: LicenciaViewModel = viewModel()
) {
    val negocioUiState by negocioViewModel.uiState.collectAsState()
    val licenciaUiState by licenciaViewModel.uiState.collectAsState()
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        negocioViewModel.loadNegocios()
    }

    // Cuando se selecciona un negocio, cargar su licencia
    LaunchedEffect(selectedNegocioId) {
        selectedNegocioId?.let {
            licenciaViewModel.loadLicencias(it)
        }
    }

    // Tomar el primer negocio si hay y no hay seleccionado
    LaunchedEffect(negocioUiState.negocios) {
        if (selectedNegocioId == null && negocioUiState.negocios.isNotEmpty()) {
            selectedNegocioId = negocioUiState.negocios.first().id
        }
    }

    // Obtener la licencia activa (la primera de la lista)
    val licenciaActiva = licenciaUiState.licencias.firstOrNull()
    val estadoLicencia = licenciaActiva?.getEstado() ?: "Sin licencia"
    val diasRestantes = licenciaActiva?.getDiasRestantes() ?: 0

    val pendientesCount = 0 // TODO: Implementar contador real

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Panel de Control") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(modifier = Modifier.weight(1f).padding(4.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏢", style = MaterialTheme.typography.headlineMedium)
                        Text("${negocioUiState.negocios.size}", style = MaterialTheme.typography.titleLarge)
                        Text("Negocios", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Card(modifier = Modifier.weight(1f).padding(4.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✅", style = MaterialTheme.typography.headlineMedium)
                        Text("${negocioUiState.negocios.filter { it.activo }.size}", style = MaterialTheme.typography.titleLarge)
                        Text("Activos", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f).padding(4.dp),
                    onClick = onPendientesClick
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⏳", style = MaterialTheme.typography.headlineMedium)
                        Text("$pendientesCount", style = MaterialTheme.typography.titleLarge)
                        Text("Pendientes", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 📜 Estado de la licencia (NUEVO)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = when {
                        licenciaUiState.licencias.isEmpty() -> Color(0xFFFF9800) // Naranja - Sin licencia
                        diasRestantes > 25 -> Color(0xFF4CAF50) // Verde - Vigente
                        diasRestantes > 4 -> Color(0xFFFFC107) // Amarillo - Próximo a vencer
                        else -> Color(0xFFF44336) // Rojo - Expirada
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🔐 Licencia",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = when {
                                licenciaUiState.licencias.isEmpty() -> "⚠️ Sin licencia registrada"
                                diasRestantes > 25 -> "✅ Vigente ($diasRestantes días)"
                                diasRestantes > 4 -> "⚠️ Próxima a vencer ($diasRestantes días)"
                                diasRestantes > 0 -> "🔴 Por vencer ($diasRestantes días)"
                                else -> "❌ EXPIRADA"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        if (licenciaActiva != null) {
                            Text(
                                text = "Device: ${licenciaActiva.device_id.take(8)}...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                    if (licenciaUiState.licencias.isNotEmpty()) {
                        Text(
                            text = when {
                                diasRestantes > 25 -> "✅"
                                diasRestantes > 4 -> "⚠️"
                                diasRestantes > 0 -> "🔴"
                                else -> "❌"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📋 Últimos negocios",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (negocioUiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(negocioUiState.negocios.take(10)) { negocio ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = negocio.nombre_negocio,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "ID: ${negocio.id} | ${if (negocio.activo) "🟢 Activo" else "🔴 Inactivo"}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        selectedNegocioId = negocio.id
                                        onNegocioClick(negocio.id)
                                    }
                                ) {
                                    Text("📂")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
