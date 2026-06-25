package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Negocio

class NegocioRepository {

    suspend fun getNegocios(): List<Negocio> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("clientes").select().decodeAs<List<Negocio>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createNegocio(nombre: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("clientes").insert(mapOf("nombre_negocio" to nombre, "activo" to true))
            true
        } catch (e: Exception) { false }
    }

    suspend fun updateNegocio(id: String, nombre: String, activo: Boolean): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("clientes")
                .update(mapOf("nombre_negocio" to nombre, "activo" to activo))
                { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteNegocio(id: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("clientes").delete { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }
}
