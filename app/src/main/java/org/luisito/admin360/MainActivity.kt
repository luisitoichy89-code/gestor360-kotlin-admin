package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.luisito.admin360.ui.components.AdminNavigationDrawer
import org.luisito.admin360.ui.screens.*
import org.luisito.admin360.ui.screens.adminlogin.AdminLoginScreen
import org.luisito.admin360.ui.screens.adminlogin.AdminLoginViewModel
import org.luisito.admin360.ui.theme.Gestor360Theme
import org.luisito.admin360.utils.PreferenceManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = PreferenceManager(this)

        setContent {
            Gestor360AdminApp(prefs = prefs)
        }
    }
}

@Composable
fun Gestor360AdminApp(prefs: PreferenceManager) {
    var isLoggedIn by remember { mutableStateOf(prefs.isLoggedIn()) }
    var selectedItem by remember { mutableStateOf("dashboard") }
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val loginViewModel: AdminLoginViewModel = viewModel()
    val loginState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(loginState.isLoggedIn) {
        if (loginState.isLoggedIn) {
            prefs.saveLogin(loginState.userId)
            isLoggedIn = true
        }
    }

    if (!isLoggedIn && !loginState.isLoggedIn) {
        AdminLoginScreen(
            viewModel = loginViewModel
        )
    } else {
        AdminNavigationDrawer(
            drawerState = drawerState,
            selectedItem = selectedItem,
            onItemClick = { item ->
                when (item) {
                    "logout" -> {
                        prefs.clear()
                        loginViewModel.resetState()
                        isLoggedIn = false
                        selectedItem = "dashboard"
                    }
                    else -> {
                        selectedItem = item
                        CoroutineScope(Dispatchers.Main).launch {
                            drawerState.close()
                        }
                    }
                }
            }
        ) {
            when (selectedItem) {
                "negocios" -> NegociosScreen(
                    onBack = { selectedItem = "dashboard" },
                    onNegocioSeleccionado = { id ->
                        selectedNegocioId = id
                        selectedItem = "locales"
                    }
                )
                "locales" -> {
                    if (selectedNegocioId != null) {
                        LocalesScreen(
                            clienteId = selectedNegocioId!!,
                            onBack = { selectedItem = "negocios" }
                        )
                    } else {
                        NegociosScreen(
                            onBack = { selectedItem = "dashboard" },
                            onNegocioSeleccionado = { id ->
                                selectedNegocioId = id
                                selectedItem = "locales"
                            }
                        )
                    }
                }
                "usuarios" -> {
                    if (selectedNegocioId != null) {
                        AdminUsersScreen(
                            clienteId = selectedNegocioId!!,
                            onBack = { selectedItem = "negocios" }
                        )
                    } else {
                        NegociosScreen(
                            onBack = { selectedItem = "dashboard" },
                            onNegocioSeleccionado = { id ->
                                selectedNegocioId = id
                                selectedItem = "usuarios"
                            }
                        )
                    }
                }
                "licencias" -> {
                    if (selectedNegocioId != null) {
                        LicenciasScreen(
                            clienteId = selectedNegocioId!!,
                            onBack = { selectedItem = "negocios" }
                        )
                    } else {
                        NegociosScreen(
                            onBack = { selectedItem = "dashboard" },
                            onNegocioSeleccionado = { id ->
                                selectedNegocioId = id
                                selectedItem = "licencias"
                            }
                        )
                    }
                }
                else -> AdminDashboardScreen(
                    onMenuClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            drawerState.open()
                        }
                    },
                    onNegocioClick = { id ->
                        selectedNegocioId = id
                        selectedItem = "locales"
                    }
                )
            }
        }
    }
}
