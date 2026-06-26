package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Negocio

class NegocioRepository {

    suspend fun getNegocios(): List<Negocio> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("negocios")
                .select()
                .decodeList<Negocio>()
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error al cargar negocios"
            emptyList()
        }
    }

    suspend fun createNegocio(nombre: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("negocios")
                .insert(
                    mapOf(
                        "nombre_negocio" to nombre,
                        "activo" to true
                    )
                )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error al crear negocio"
            false
        }
    }

    suspend fun addNegocio(negocio: Negocio): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("negocios")
                .insert(negocio)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error al agregar negocio"
            false
        }
    }

    suspend fun updateNegocio(negocio: Negocio): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("negocios")
                .update(negocio) {
                    filter { eq("id", negocio.id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error al actualizar negocio"
            false
        }
    }

    suspend fun deleteNegocio(id: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("negocios")
                .delete {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            ErrorHolder.lastError = e.message ?: "Error al eliminar negocio"
            false
        }
    }
}

object ErrorHolder {
    var lastError: String = ""
}
