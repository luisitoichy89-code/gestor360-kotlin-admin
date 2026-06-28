package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Negocio
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object ErrorHolder {
    var lastError: String = ""
}

class NegocioRepository {

    suspend fun getNegocios(): List<Negocio> {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("clientes")
                .select(Columns.ALL)
                .decodeAs<List<Negocio>>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createNegocio(nombre: String): Boolean {
        return try {
            val data = buildJsonObject {
                put("nombre_negocio", nombre)
                put("activo", true)
            }
            SupabaseClientProvider.client
                .postgrest.from("clientes")
                .insert(data) {
                    select(Columns.ALL)
                }
                .decodeAs<List<Negocio>>()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error desconocido"
            false
        }
    }

    suspend fun updateNegocio(id: Int, nombre: String, activo: Boolean): Boolean {
        return try {
            val data = buildJsonObject {
                put("nombre_negocio", nombre)
                put("activo", activo)
            }
            SupabaseClientProvider.client
                .postgrest.from("clientes")
                .update(data) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteNegocio(id: Int): Boolean {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("clientes")
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
