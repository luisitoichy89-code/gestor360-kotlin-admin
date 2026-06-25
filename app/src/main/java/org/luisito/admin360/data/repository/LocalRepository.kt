package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Local

class LocalRepository {

    suspend fun getLocales(clienteId: String): List<Local> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("locales")
                .select { filter { eq("cliente_id", clienteId) } }
                .decodeAs<List<Local>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createLocal(clienteId: String, nombre: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("locales").insert(mapOf(
                "cliente_id" to clienteId,
                "nombre" to nombre,
                "activo" to true
            ))
            true
        } catch (e: Exception) { false }
    }

    suspend fun updateLocal(id: Int, nombre: String, activo: Boolean): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("locales")
                .update(mapOf("nombre" to nombre, "activo" to activo))
                { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteLocal(id: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("locales").delete { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }
}
