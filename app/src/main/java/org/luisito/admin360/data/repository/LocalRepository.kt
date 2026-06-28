package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Local
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LocalRepository {

    suspend fun getLocales(clienteId: Int): List<Local> {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("locales")
                .select(Columns.ALL) {
                    filter { eq("cliente_id", clienteId) }
                }
                .decodeAs<List<Local>>()
        } catch (e: Exception) { 
            e.printStackTrace()
            emptyList() 
        }
    }

    suspend fun createLocal(clienteId: Int, nombre: String): Boolean {
        return try {
            val data = buildJsonObject {
                put("cliente_id", clienteId)
                put("nombre", nombre)
                put("activo", true)
            }
            SupabaseClientProvider.client
                .postgrest.from("locales")
                .insert(data) {
                    select(Columns.ALL)
                }
                .decodeAs<List<Local>>()
            true
        } catch (e: Exception) { 
            e.printStackTrace()
            false 
        }
    }

    suspend fun updateLocal(id: Int, nombre: String, activo: Boolean): Boolean {
        return try {
            val data = buildJsonObject {
                put("nombre", nombre)
                put("activo", activo)
            }
            SupabaseClientProvider.client
                .postgrest.from("locales")
                .update(data) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) { 
            e.printStackTrace()
            false 
        }
    }

    suspend fun deleteLocal(id: Int): Boolean {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("locales")
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
