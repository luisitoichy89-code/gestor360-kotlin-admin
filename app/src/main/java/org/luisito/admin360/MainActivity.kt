package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.luisito.admin360.ui.screens.AdminDashboardScreen
import org.luisito.admin360.ui.screens.AdminUsersScreen
import org.luisito.admin360.ui.screens.LicenciasScreen
import org.luisito.admin360.ui.screens.LocalesScreen
import org.luisito.admin360.ui.screens.NegociosScreen
import org.luisito.admin360.ui.screens.TrazaScreen
import org.luisito.admin360.ui.screens.login.LoginScreen
import org.luisito.admin360.ui.theme.Gestor360Theme
import org.luisito.admin360.ui.viewmodels.LocalViewModel
import org.luisito.admin360.ui.viewmodels.NegocioViewModel
import org.luisito.admin360.ui.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Gestor360Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var selectedClienteId by remember { mutableStateOf<String?>(null) }
    var selectedAlmacenId by remember { mutableStateOf<String?>(null) }

    val negocioViewModel: NegocioViewModel = viewModel()
    val localViewModel: LocalViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    // Si no está logueado, mostrar Login
    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
                currentScreen = Screen.Dashboard
            }
        )
        return
    }

    // Si está logueado, mostrar la navegación normal
    when (currentScreen) {
        is Screen.Login -> {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    currentScreen = Screen.Dashboard
                }
            )
        }
        is Screen.Dashboard -> {
            AdminDashboardScreen(
                onMenuClick = { /* Abrir drawer */ },
                onNegocioClick = { negocioId ->
                    selectedClienteId = negocioId
                    currentScreen = Screen.Negocios
                },
                onPendientesClick = { /* TODO */ },
                onLocalesClick = { clienteId ->
                    selectedClienteId = clienteId
                    currentScreen = Screen.Locales
                },
                onUsuariosClick = { clienteId ->
                    selectedClienteId = clienteId
                    currentScreen = Screen.Usuarios
                },
                onLicenciasClick = { clienteId ->
                    selectedClienteId = clienteId
                    currentScreen = Screen.Licencias
                },
                onTrazasClick = { almacenId ->
                    selectedAlmacenId = almacenId
                    currentScreen = Screen.Trazas
                }
            )
        }
        is Screen.Negocios -> {
            NegociosScreen(
                onBack = { currentScreen = Screen.Dashboard },
                onNegocioSeleccionado = { negocioId ->
                    selectedClienteId = negocioId
                    currentScreen = Screen.Dashboard
                }
            )
        }
        is Screen.Locales -> {
            LocalesScreen(
                clienteId = selectedClienteId ?: "",
                onBack = { currentScreen = Screen.Dashboard },
                viewModel = localViewModel
            )
        }
        is Screen.Usuarios -> {
            val locales = localViewModel.uiState.value.locales
            AdminUsersScreen(
                clienteId = selectedClienteId ?: "",
                locales = locales,
                onBack = { currentScreen = Screen.Dashboard },
                viewModel = userViewModel
            )
        }
        is Screen.Licencias -> {
            LicenciasScreen(
                clienteId = selectedClienteId ?: "",
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        is Screen.Trazas -> {
            TrazaScreen(
                almacenId = selectedAlmacenId,
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object Negocios : Screen()
    object Locales : Screen()
    object Usuarios : Screen()
    object Licencias : Screen()
    object Trazas : Screen()
}
