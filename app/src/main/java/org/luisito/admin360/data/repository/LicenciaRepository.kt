package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import java.time.LocalDate

class LicenciaRepository {
    suspend fun getLicencias(clienteId: String): List<Map<String, Any>> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("licencias").select { filter { eq("cliente_id", clienteId) } }.decodeAs<List<Map<String, Any>>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createLicencia(clienteId: String, deviceId: String, dias: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            supabase.from("licencias").insert(mapOf(
                "cliente_id" to clienteId, "device_id" to deviceId, "expiracion" to expiracion, "activo" to true
            ))
            true
        } catch (e: Exception) { false }
    }
}
