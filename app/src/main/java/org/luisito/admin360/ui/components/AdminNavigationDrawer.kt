package org.luisito.admin360.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    var gestionExpanded by remember { mutableStateOf(true) }
    var adminExpanded by remember { mutableStateOf(true) }

    LaunchedEffect(clienteId) {
        if (clienteId != null) {
            licenciaViewModel.loadLicencias(clienteId)
        }
    }

    val diasRestantes = if (clienteId != null) {
        licenciaViewModel.getDiasRestantes(clienteId)
    } else 0

    val licenseColor = when {
        diasRestantes > 25 -> MaterialTheme.colorScheme.primary
        diasRestantes > 4 -> MaterialTheme.colorScheme.tertiary
        diasRestantes >= 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏢 Gestor360 Admin",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (clienteId != null) {
                        Text(
                            text = "$diasRestantes días",
                            color = licenseColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxSize()) {
                    Text(                        text = "GESTIÓN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("📊 Dashboard") },
                        selected = selectedItem == "dashboard",
                        onClick = { scope.launch { drawerState.close(); onItemClick("dashboard") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text("🏢 Negocios") },
                        selected = selectedItem == "negocios",
                        onClick = { scope.launch { drawerState.close(); onItemClick("negocios") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text("🏪 Locales") },
                        selected = selectedItem == "locales",
                        onClick = { scope.launch { drawerState.close(); onItemClick("locales") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ADMINISTRACIÓN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    NavigationDrawerItem(
                        label = { Text("👥 Usuarios") },
                        selected = selectedItem == "usuarios",
                        onClick = { scope.launch { drawerState.close(); onItemClick("usuarios") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text("🔑 Licencias") },
                        selected = selectedItem == "licencias",
                        onClick = { scope.launch { drawerState.close(); onItemClick("licencias") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    NavigationDrawerItem(
                        label = { Text("⏳ Control de Licencias") },
                        selected = selectedItem == "control",
                        onClick = { scope.launch { drawerState.close(); onItemClick("control") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        label = { Text("🚪 Cerrar Sesión") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close(); onItemClick("logout") } },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedTextColor = MaterialTheme.colorScheme.error                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        content = content
    )
}
