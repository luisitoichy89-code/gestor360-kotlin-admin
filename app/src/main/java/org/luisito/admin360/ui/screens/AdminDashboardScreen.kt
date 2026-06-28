package org.luisito.admin360.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel
import org.luisito.admin360.ui.viewmodels.NegocioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onMenuClick: () -> Unit,
    onNegocioClick: (Int) -> Unit,
    onPendientesClick: () -> Unit,
    negocioViewModel: NegocioViewModel = viewModel(),
    licenciaViewModel: LicenciaViewModel = viewModel()
) {
    val negocioUiState by negocioViewModel.uiState.collectAsState()
    val licenciaUiState by licenciaViewModel.uiState.collectAsState()
    var selectedNegocioId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    val drawerState = androidx.compose.material3.rememberDrawerState(initialValue = DrawerValue.Closed)

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
    val estadoLicencia = licenciaActiva?.getEstado() ?: "Sin licencia"
    val diasRestantes = licenciaActiva?.getDiasRestantes() ?: 0

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Gestor360 Admin", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                androidx.compose.material3.Divider()
                listOf("Negocios", "Locales", "Usuarios", "Licencias", "Cerrar Sesión").forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            when (item) {
                                "Cerrar Sesión" -> { /* logout */ }
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("📊 Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
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
                // Estado de licencia
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = when {
                            diasRestantes > 25 -> Color(0xFF4CAF50)
                            diasRestantes > 4 -> Color(0xFFFFC107)
                            diasRestantes >= 0 -> Color(0xFFFF5722)
                            else -> Color(0xFF9E9E9E)
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("📜 Licencia: $estadoLicencia", style = MaterialTheme.typography.titleMedium)
                        Text("Días restantes: $diasRestantes", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Selector de negocio
                if (negocioUiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    Text("Seleccionar negocio:", style = MaterialTheme.typography.titleSmall)
                    androidx.compose.material3.DropdownMenu(
                        expanded = false,
                        onDismissRequest = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        negocioUiState.negocios.forEach { negocio ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(negocio.nombre_negocio) },
                                onClick = { selectedNegocioId = negocio.id }
                            )
                        }
                    }
                    // Mostrar selector simple
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        negocioUiState.negocios.forEach { negocio ->
                            androidx.compose.material3.Button(
                                onClick = { selectedNegocioId = negocio.id },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = if (selectedNegocioId == negocio.id) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(negocio.nombre_negocio.take(10))
                            }
                        }
                    }
                }

                // Lista de locales del negocio seleccionado
                if (selectedNegocioId != null) {
                    Text("Locales del negocio:", style = MaterialTheme.typography.titleSmall)
                    // Aquí se cargarían los locales
                    Text("Selecciona un negocio para ver sus locales", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
