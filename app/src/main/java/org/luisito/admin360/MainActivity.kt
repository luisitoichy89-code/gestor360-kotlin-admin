package org.luisito.admin360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.luisito.admin360.ui.components.AdminNavigationDrawer
import org.luisito.admin360.ui.screens.AdminDashboardScreen
import org.luisito.admin360.ui.screens.AdminUsersScreen
import org.luisito.admin360.ui.screens.LicenciasScreen
import org.luisito.admin360.ui.screens.LocalesScreen
import org.luisito.admin360.ui.screens.NegociosScreen
import org.luisito.admin360.ui.screens.PendientesScreen
import org.luisito.admin360.ui.screens.adminlogin.AdminLoginScreen
import org.luisito.admin360.ui.screens.adminlogin.AdminLoginViewModel
import org.luisito.admin360.ui.theme.Gestor360Theme
import org.luisito.admin360.utils.PreferenceManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContent {
                Gestor360AdminApp()
            }
        } catch (e: Exception) {
            val errorFile = java.io.File(filesDir, "gestor360_admin_crash.txt")
            errorFile.writeText("Error en onCreate: ${e.message}\n${e.stackTraceToString()}")
            finish()
        }
    }
}

@Composable
fun Gestor360AdminApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("dashboard") }
    var selectedNegocioId by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val prefs = PreferenceManager(androidx.compose.ui.platform.LocalContext.current)
    val loginViewModel: AdminLoginViewModel = viewModel()
    val loginState by loginViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        isLoggedIn = prefs.isLoggedIn()
    }

    if (!isLoggedIn && !loginState.isLoggedIn) {
        AdminLoginScreen(
            viewModel = loginViewModel
        )
    } else {
        AdminNavigationDrawer(
            drawerState = drawerState,
            selectedItem = selectedItem,
            clienteId = selectedNegocioId,
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
                "pendientes" -> {
                    PendientesScreen(
                        onBack = { selectedItem = "dashboard" }
                    )
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
                    },
                    onPendientesClick = {
                        selectedItem = "pendientes"
                    }
                )
            }
        }
    }
}
