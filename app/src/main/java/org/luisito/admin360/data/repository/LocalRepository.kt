package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.remote.SupabaseProvider

class LocalRepository {

    private val client = SupabaseProvider.client

    suspend fun getLocales(clienteId: String): Result<List<Local>> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("locales")
                    .select {
                        filter {
                            eq("cliente_id", clienteId)
                        }
                    }
                    .decodeList<Local>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createLocal(
        clienteId: String,
        nombre: String
    ): Result<Local> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("locales")
                    .insert(
                        mapOf(
                            "cliente_id" to clienteId,
                            "nombre" to nombre,
                            "activo" to true
                        )
                    )
                    .decodeSingle<Local>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateLocal(
        id: String,
        nombre: String,
        activo: Boolean
    ): Result<Local> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("locales")
                    .update(
                        {
                            set("nombre", nombre)
                            set("activo", activo)
                        }
                    ) {
                        filter {
                            eq("id", id)
                        }
                    }
                    .decodeSingle<Local>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteLocal(id: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {

                client
                    .from("locales")
                    .delete {
                        filter {
                            eq("id", id)
                        }
                    }

                Result.success(true)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
