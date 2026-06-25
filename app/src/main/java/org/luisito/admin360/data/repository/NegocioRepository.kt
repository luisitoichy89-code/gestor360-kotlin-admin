package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider

class NegocioRepository {
    suspend fun getNegocios(): List<Map<String, Any>> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("clientes").select().decodeAs<List<Map<String, Any>>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createNegocio(nombre: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("clientes").insert(mapOf("nombre_negocio" to nombre, "activo" to true))
            true
        } catch (e: Exception) { false }
    }
}
