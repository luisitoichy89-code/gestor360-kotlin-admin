package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.luisito.admin360.ui.screens.AdminDashboardScreen
import org.luisito.admin360.ui.screens.LicenciasScreen
import org.luisito.admin360.ui.screens.LocalesScreen
import org.luisito.admin360.ui.screens.NegociosScreen
import org.luisito.admin360.ui.screens.UsuariosScreen
import org.luisito.admin360.ui.theme.Admin360Theme
import org.luisito.admin360.ui.viewmodels.LicenciaViewModel
import org.luisito.admin360.ui.viewmodels.LocalViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Admin360Theme {
                Surface(
                    modifier = Modifier.padding(0.dp),
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
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "negocios") {
        composable("negocios") {
            NegociosScreen(
                onBack = { /* Cerrar app o ir atrás */ },
                onNegocioSeleccionado = { negocioId ->
                    navController.navigate("dashboard/$negocioId")
                }
            )
        }
        composable("dashboard/{negocioId}") { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getString("negocioId") ?: ""
            AdminDashboardScreen(
                onMenuClick = { /* Abrir drawer */ },
                onNegocioClick = { /* Navegar a detalle */ },
                onPendientesClick = { /* Navegar a pendientes */ }
            )
        }
        composable("locales/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId") ?: ""
            LocalesScreen(
                clienteId = clienteId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("usuarios/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId") ?: ""
            UsuariosScreen(
                clienteId = clienteId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("licencias/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId") ?: ""
            LicenciasScreen(
                clienteId = clienteId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
