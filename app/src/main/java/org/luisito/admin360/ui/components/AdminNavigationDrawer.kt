import androidx.compose.runtime.*
package org.luisito.admin360.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel

@Composable
fun AdminNavigationDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    selectedItem: String,
    onItemClick: (String) -> Unit,
    clienteId: String? = null,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val licenciaViewModel: LicenciaViewModel = viewModel()
    val uiState by licenciaViewModel.uiState.collectAsState()

    LaunchedEffect(clienteId) {
        if (clienteId != null) {
            licenciaViewModel.loadLicencias(clienteId)
        }
    }

    val diasRestantes = if (clienteId != null) {
        licenciaViewModel.getDiasRestantes(clienteId)
    } else 0

    val color = when {
        diasRestantes > 25 -> Color.Green
        diasRestantes > 4 -> Color.Yellow
        diasRestantes >= 0 -> Color.Red
        else -> Color.Gray
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🏢 Gestor360 Admin",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (clienteId != null) {
                        Text(
                            text = "$diasRestantes días",
                            color = color,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Column(modifier = Modifier.fillMaxSize()) {
                    NavigationDrawerItem(
                        label = { Text("📊 Dashboard") },
                        selected = selectedItem == "dashboard",
                        onClick = { scope.launch { drawerState.close(); onItemClick("dashboard") } },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("🏢 Negocios") },
                        selected = selectedItem == "negocios",
                        onClick = { scope.launch { drawerState.close(); onItemClick("negocios") } },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("🏪 Locales") },
                        selected = selectedItem == "locales",
                        onClick = { scope.launch { drawerState.close(); onItemClick("locales") } },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("👥 Usuarios") },
                        selected = selectedItem == "usuarios",
                        onClick = { scope.launch { drawerState.close(); onItemClick("usuarios") } },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("🔑 Licencias") },
                        selected = selectedItem == "licencias",
                        onClick = { scope.launch { drawerState.close(); onItemClick("licencias") } },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("⏳ Control de Licencias") },
                        selected = selectedItem == "control",
                        onClick = { scope.launch { drawerState.close(); onItemClick("control") } },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("🚪 Cerrar Sesión") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close(); onItemClick("logout") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        },
        content = content
    )
}
