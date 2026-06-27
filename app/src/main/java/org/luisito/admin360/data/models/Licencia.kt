package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Serializable
data class Licencia(
    val id: String,  // UUID de Supabase
    val cliente_id: String,
    val device_id: String,
    val expiracion: String?,
    val activo: Boolean,
    val created_at: String?
) {
    fun getDiasRestantes(): Int {
        if (expiracion == null) return 0
        return try {
            val expDate = LocalDate.parse(expiracion)
            val now = LocalDate.now()
            ChronoUnit.DAYS.between(now, expDate).toInt()
        } catch (e: Exception) {
            0
        }
    }

    fun getEstado(): String {
        val dias = getDiasRestantes()
        return when {
            dias > 25 -> "🟢 Vigente"
            dias > 4 -> "🟡 Próximo a vencer"
            dias >= 0 -> "🔴 Por vencer"
            else -> "⚫ Expirada"
        }
    }
}
