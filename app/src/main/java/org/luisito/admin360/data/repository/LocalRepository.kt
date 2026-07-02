package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.remote.SupabaseProvider

class LocalRepository {

    suspend fun getLocales(negocioId: String): Result<List<Local>> {
        return try {
            val response = SupabaseProvider.client
                .from("locales")
                .select {
                    filter {
                        eq("negocio_id", negocioId)
                    }
                }
            Result.success(response.decodeList<Local>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createLocal(
        nombre: String,
        direccion: String,
        telefono: String,
        negocioId: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .insert(
                    mapOf(
                        "nombre" to nombre,
                        "direccion" to direccion,
                        "telefono" to telefono,
                        "negocio_id" to negocioId
                    )
                )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLocal(
        id: Int,
        nombre: String,
        direccion: String,
        telefono: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .update(
                    mapOf(
                        "nombre" to nombre,
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

    suspend fun setActivo(id: Int, activo: Boolean): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
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

    suspend fun deleteLocal(id: Int): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
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
