package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider

class LocalRepository {
    suspend fun getLocales(clienteId: String): List<Map<String, Any>> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("locales").select { filter { eq("cliente_id", clienteId) } }.decodeAs<List<Map<String, Any>>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createLocal(clienteId: String, nombre: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("locales").insert(mapOf("cliente_id" to clienteId, "nombre" to nombre, "activo" to true))
            true
        } catch (e: Exception) { false }
    }
}
