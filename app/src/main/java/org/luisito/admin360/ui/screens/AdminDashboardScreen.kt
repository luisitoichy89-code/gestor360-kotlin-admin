package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
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
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel
import org.luisito.admin360.ui.viewmodels.NegocioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onMenuClick: () -> Unit,
    onNegocioClick: (String) -> Unit,
    onPendientesClick: () -> Unit,
    onLocalesClick: (String) -> Unit,
    onUsuariosClick: (String) -> Unit,
    onLicenciasClick: (String) -> Unit,
    onTrazasClick: (String?) -> Unit,
    negocioViewModel: NegocioViewModel = viewModel(),
    licenciaViewModel: LicenciaViewModel = viewModel()
) {
    val negocioUiState by negocioViewModel.uiState.collectAsState()
    val licenciaUiState by licenciaViewModel.uiState.collectAsState()
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        negocioViewModel.loadNegocios()
    }

    LaunchedEffect(selectedNegocioId) {
        selectedNegocioId?.let {
            licenciaViewModel.loadLicencias(it)
        }
    }

    LaunchedEffect(negocioUiState.negocios) {
        if (selectedNegocioId == null && negocioUiState.negocios.isNotEmpty()) {
            selectedNegocioId = negocioUiState.negocios.first().id
        }
    }

    val licenciaActiva = licenciaUiState.licencias.firstOrNull()
    val diasRestantes = licenciaActiva?.getDiasRestantes() ?: 0

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = when {
                        licenciaUiState.licencias.isEmpty() -> Color(0xFFFF9800)
                        diasRestantes > 25 -> Color(0xFF4CAF50)
                        diasRestantes > 4 -> Color(0xFFFFC107)
                        diasRestantes >= 0 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
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
                                diasRestantes >= 0 -> "🔴 Por vencer ($diasRestantes días)"
                                else -> "❌ EXPIRADA"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Text(
                        text = when {
                            diasRestantes > 25 -> "✅"
                            diasRestantes > 4 -> "⚠️"
                            diasRestantes >= 0 -> "🔴"
                            else -> "❌"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📋 Módulos",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModuloCard(
                    icono = "🏢",
                    titulo = "Negocios",
                    onClick = { /* ya está en el menú */ }
                )
                ModuloCard(
                    icono = "📍",
                    titulo = "Locales",
                    onClick = { selectedNegocioId?.let { onLocalesClick(it) } }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModuloCard(
                    icono = "👥",
                    titulo = "Usuarios",
                    onClick = { selectedNegocioId?.let { onUsuariosClick(it) } }
                )
                ModuloCard(
                    icono = "📜",
                    titulo = "Licencias",
                    onClick = { selectedNegocioId?.let { onLicenciasClick(it) } }
                )
                ModuloCard(
                    icono = "📋",
                    titulo = "Trazas",
                    onClick = { onTrazasClick(null) }
                )
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

@Composable
fun ModuloCard(
    icono: String,
    titulo: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icono,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
