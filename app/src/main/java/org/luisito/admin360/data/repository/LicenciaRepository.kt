package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Licencia
import java.time.LocalDate

class LicenciaRepository {

    suspend fun getLicencias(clienteId: String): List<Licencia> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("licencias")
                .select { filter { eq("cliente_id", clienteId) } }
                .decodeAs<List<Licencia>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun activateLicense(clienteId: String, deviceId: String, dias: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            supabase.from("licencias").insert(mapOf(
                "cliente_id" to clienteId,
                "device_id" to deviceId,
                "expiracion" to expiracion,
                "activo" to true
            ))
            true
        } catch (e: Exception) { false }
    }

    suspend fun renewLicense(clienteId: String, dias: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            supabase.from("licencias")
                .update(mapOf("expiracion" to expiracion, "activo" to true))
                { filter { eq("cliente_id", clienteId) } }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteLicense(id: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("licencias").delete { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }
}
