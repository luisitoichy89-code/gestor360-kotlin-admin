package org.luisito.admin360.ui.core

import androidx.compose.runtime.*
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.ui.screens.*

private sealed class Pantalla {
    object Dashboard : Pantalla()
    object Negocios : Pantalla()
    data class NegocioDetail(val negocio: Negocio) : Pantalla()
    object Almacenamiento : Pantalla()
    object Tickets : Pantalla()
}

@Composable
fun AppContent() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Dashboard) }
    var negocioActivo by remember { mutableStateOf<Negocio?>(null) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
        return
    }

    when (val pantalla = pantallaActual) {
        is Pantalla.Dashboard -> AdminDashboardScreen(
            negocioActivo = negocioActivo,
            onNavigate = { route ->
                pantallaActual = when (route) {
                    "negocios" -> Pantalla.Negocios
                    "almacenamiento" -> Pantalla.Almacenamiento
                    "tickets" -> Pantalla.Tickets
                    "gestionar" -> negocioActivo?.let { Pantalla.NegocioDetail(it) } ?: Pantalla.Dashboard
                    else -> Pantalla.Dashboard
                }
            },
            onLogout = {
                isLoggedIn = false
                pantallaActual = Pantalla.Dashboard
                negocioActivo = null
            }
        )

        is Pantalla.Negocios -> NegociosScreen(
            onBack = { pantallaActual = Pantalla.Dashboard },
            onSeleccionarNegocio = { negocio ->
                negocioActivo = negocio
                pantallaActual = Pantalla.NegocioDetail(negocio)
            }
        )

        is Pantalla.NegocioDetail -> NegocioDetailScreen(
            negocio = pantalla.negocio,
            onBack = { pantallaActual = Pantalla.Dashboard }
        )

        is Pantalla.Tickets -> TicketsAdminScreen(onBack = { pantallaActual = Pantalla.Dashboard })
        is Pantalla.Almacenamiento -> AlmacenamientoScreen(
            onBack = { pantallaActual = Pantalla.Dashboard }
        )
    }
}
