package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.data.remote.SupabaseProvider

class NegocioRepository {

    /** Todos los negocios del sistema, sin filtrar por cliente. Vista de superadmin. */
    suspend fun getAllNegocios(): Result<List<Negocio>> {
        return try {
            val response = SupabaseProvider.client
                .from("clientes")
                .select()
            Result.success(response.decodeList<Negocio>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNegocios(clienteId: String): Result<List<Negocio>> {
        return try {
            val response = SupabaseProvider.client
                .from("clientes")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            Result.success(response.decodeList<Negocio>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNegocio(
        nombre: String,
        direccion: String,
        telefono: String,
        clienteId: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .insert(
                    mapOf(
                        "nombre_negocio" to nombre,
                        "direccion" to direccion,
                        "telefono" to telefono,
                        "cliente_id" to clienteId
                    )
                )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNegocio(
        id: String,
        nombre: String,
        direccion: String,
        telefono: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .update(
                    mapOf(
                        "nombre_negocio" to nombre,
                        "direccion" to direccion,
                        "telefono" to telefono
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setActivo(id: String, activo: Boolean): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .update(mapOf("activo" to activo)) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNegocio(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
