package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class Licencia(
    val id: String? = null,
    val cliente_id: String,
    val device_id: String,
    val expiracion: String,
    val activo: Boolean = true
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

fun Licencia.getDiasRestantes(): Long {
    return try {
        val formatter = java.time.format.DateTimeFormatter.ISO_DATE
        val expDate = java.time.LocalDate.parse(expiracion, formatter)
        val today = java.time.LocalDate.now()
        java.time.temporal.ChronoUnit.DAYS.between(today, expDate)
    } catch (e: Exception) {
        -1
    }
}
