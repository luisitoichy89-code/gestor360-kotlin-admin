package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Negocio

object ErrorHolder {
    var lastError: String = ""
}

class NegocioRepository {

    suspend fun getNegocios(): List<Negocio> {
        return try {
            SupabaseClientProvider.client
                .from("clientes")
                .select()
                .decodeAs<List<Negocio>>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createNegocio(nombre: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("clientes")
                .insert(mapOf("nombre_negocio" to nombre, "activo" to true))
                .select()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error desconocido"
            false
        }
    }

    suspend fun updateNegocio(id: String, nombre: String, activo: Boolean): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("clientes")
                .update(mapOf("nombre_negocio" to nombre, "activo" to activo)) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteNegocio(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("clientes")
                .delete {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
