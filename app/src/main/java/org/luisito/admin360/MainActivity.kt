package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.luisito.admin360.ui.screens.AdminDashboardScreen
import org.luisito.admin360.ui.screens.LicenciasScreen
import org.luisito.admin360.ui.screens.LocalesScreen
import org.luisito.admin360.ui.screens.NegociosScreen
import org.luisito.admin360.ui.screens.UsuariosScreen
import org.luisito.admin360.ui.theme.Admin360Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Admin360Theme {
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
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "negocios") {
        composable("negocios") {
            NegociosScreen(
                onBack = { /* Cerrar app */ },
                onNegocioSeleccionado = { negocioId ->
                    navController.navigate("dashboard/$negocioId")
                }
            )
        }
        composable("dashboard/{negocioId}") { backStackEntry ->
            val negocioId = backStackEntry.arguments?.getString("negocioId")?.toIntOrNull() ?: 0
            AdminDashboardScreen(
                onMenuClick = { /* Abrir drawer */ },
                onNegocioClick = { /* Navegar a detalle */ },
                onPendientesClick = { /* Navegar a pendientes */ }
            )
        }
        composable("locales/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull() ?: 0
            LocalesScreen(
                clienteId = clienteId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("usuarios/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull() ?: 0
            UsuariosScreen(
                clienteId = clienteId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("licencias/{clienteId}") { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getString("clienteId")?.toIntOrNull() ?: 0
            LicenciasScreen(
                clienteId = clienteId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
