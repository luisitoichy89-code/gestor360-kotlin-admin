package org.luisito.admin360.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.ui.screens.AdminDashboardScreen
import org.luisito.admin360.ui.screens.LicenciasScreen
import org.luisito.admin360.ui.screens.LocalesScreen
import org.luisito.admin360.ui.screens.LoginScreen
import org.luisito.admin360.ui.screens.NegociosScreen
import org.luisito.admin360.ui.screens.UsuariosScreen

/**
 * Navegación simple basada en estado (sin Navigation-Compose todavía).
 * El "negocio activo" es el equivalente a session['negocio_activo'] del backend Flask:
 * se fija al elegir un negocio en NegociosScreen y se reutiliza en Locales/Usuarios/Licencias
 * hasta que el usuario vuelve atrás o elige otro.
 */
private sealed class Pantalla {
    object Dashboard : Pantalla()
    data class Negocios(val destinoPendiente: String? = null) : Pantalla()
    object Locales : Pantalla()
    object Usuarios : Pantalla()
    object Licencias : Pantalla()
}

@Composable
fun AppContent() {

    var isLoggedIn by remember { mutableStateOf(false) }
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Dashboard) }
    var negocioActivo by remember { mutableStateOf<Negocio?>(null) }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { isLoggedIn = true }
        )
        return
    }

    when (val pantalla = pantallaActual) {

        is Pantalla.Dashboard -> AdminDashboardScreen(
            onNavigate = { route ->
                pantallaActual = when (route) {
                    "negocios" -> Pantalla.Negocios(destinoPendiente = null)
                    "locales", "usuarios", "licencias" -> {
                        if (negocioActivo != null) {
                            // Ya hay un negocio activo de una visita anterior: ir directo.
                            when (route) {
                                "locales" -> Pantalla.Locales
                                "usuarios" -> Pantalla.Usuarios
                                else -> Pantalla.Licencias
                            }
                        } else {
                            // Sin negocio activo: primero hay que elegir uno.
                            Pantalla.Negocios(destinoPendiente = route)
                        }
                    }
                    else -> Pantalla.Dashboard
                }
            }
        )

        is Pantalla.Negocios -> NegociosScreen(
        is Pantalla.Negocios -> NegociosScreen(
            onBack = { pantallaActual = Pantalla.Dashboard },
            onSeleccionarNegocio = { negocio ->
                negocioActivo = negocio
                pantallaActual = when (pantalla.destinoPendiente) {
                    "usuarios" -> Pantalla.Usuarios
                    "licencias" -> Pantalla.Licencias
                    else -> Pantalla.Locales
                }
            }
        )

        is Pantalla.Locales -> {
            val negocio = negocioActivo
            if (negocio == null) {
                pantallaActual = Pantalla.Negocios(destinoPendiente = "locales")
            } else {
                LocalesScreen(
                    negocioId = negocio.id,
                    negocioNombre = negocio.nombre_negocio,
                    onBack = { pantallaActual = Pantalla.Negocios(destinoPendiente = null) }
                )
            }
        }

        is Pantalla.Usuarios -> {
            val negocio = negocioActivo
            val clienteId = negocio?.id
            if (negocio == null || clienteId == null) {
                pantallaActual = Pantalla.Negocios(destinoPendiente = "usuarios")
            } else {
                UsuariosScreen(
                    clienteId = clienteId,
                    negocioNombre = negocio.nombre_negocio,
                    onBack = { pantallaActual = Pantalla.Negocios(destinoPendiente = null) }
                )
            }
        }

        is Pantalla.Licencias -> {
            val negocio = negocioActivo
            if (negocio == null) {
                pantallaActual = Pantalla.Negocios(destinoPendiente = "licencias")
            } else {
                LicenciasScreen(
                    negocioId = negocio.id,
                    negocioNombre = negocio.nombre_negocio,
                    onBack = { pantallaActual = Pantalla.Negocios(destinoPendiente = null) }
                )
            }
        }
    }
}
