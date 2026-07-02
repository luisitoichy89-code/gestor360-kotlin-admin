package org.luisito.admin360.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.luisito.admin360.ui.screens.LoginScreen
import org.luisito.admin360.ui.screens.AdminDashboardScreen

@Composable
fun AppContent() {

    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {

        LoginScreen(
            onLoginSuccess = { isLoggedIn = true }
        )

    } else {

        AdminDashboardScreen(
            onNavigate = { route ->
                // TODO: Implementar navegación
            }
        )
    }
}
