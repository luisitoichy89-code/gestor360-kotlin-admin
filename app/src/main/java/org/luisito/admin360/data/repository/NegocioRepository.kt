package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Negocio

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
                .insert(mapOf(
                    "nombre_negocio" to nombre,
                    "activo" to true
                ))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error desconocido"
            false
        }
    }
}

object ErrorHolder {
    var lastError: String = ""
}
