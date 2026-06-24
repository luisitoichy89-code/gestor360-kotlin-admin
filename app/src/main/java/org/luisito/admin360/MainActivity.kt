package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import org.luisito.admin360.ui.screens.adminlogin.AdminLoginScreen
import org.luisito.admin360.ui.screens.DashboardScreen
import org.luisito.admin360.ui.theme.Gestor360AdminTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Gestor360AdminApp()
        }
    }
}

@Composable
fun Gestor360AdminApp() {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        AdminLoginScreen(
            onLoginSuccess = { isLoggedIn = true },
            onRecovery = { /* TODO: Implementar recuperación */ },
            isLoading = false,
            error = null
        )
    } else {
        DashboardScreen(
            userRol = "superadmin",
            username = "Administrador",
            onMenuClick = {},
            onLogout = { isLoggedIn = false }
        )
    }
}
