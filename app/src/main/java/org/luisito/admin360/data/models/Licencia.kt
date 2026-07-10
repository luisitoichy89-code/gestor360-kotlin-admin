package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Licencia principal del negocio (una sola por cliente/negocio, no por usuario ni por local).
 * Coincide con la tabla real "licencias": id bigint, cliente_id uuid, device_id text,
 * expiracion date, activo boolean, created_at.
 * device_id = Android ID del primer admin que registró/activó el negocio.
 * Si vence o queda inactiva, tanto admin como vendedores de TODOS los locales de ese
 * negocio quedan bloqueados en la app cliente hasta que se renueve.
 */
@Serializable
data class Licencia(
    val id: Long? = null,
    val cliente_id: String,
    val device_id: String,
    val expiracion: String,
    val activo: Boolean = true,
    val created_at: String? = null
)

fun Licencia.getDiasRestantes(): Long {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE
        val expDate = LocalDate.parse(expiracion, formatter)
        val today = LocalDate.now()
        java.time.temporal.ChronoUnit.DAYS.between(today, expDate)
    } catch (e: Exception) {
        -1
    }
}

/** true si la licencia está vencida o fue desactivada manualmente -> negocio bloqueado. */
fun Licencia.estaBloqueada(): Boolean = !activo || getDiasRestantes() < 0
