package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import io.github.jan.supabase.postgrest.query.PostgrestRequestBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.data.remote.SupabaseProvider

class NegocioRepository {

    private val client = SupabaseProvider.client

    suspend fun getNegocios(): Result<List<Negocio>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = client
                    .from("clientes")
                    .select()
                    .decodeList<Negocio>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createNegocio(nombre: String): Result<Negocio> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("clientes")
                    .insert(
                        mapOf(
                            "nombre_negocio" to nombre,
                            "activo" to true
                        )
                    )
                    .decodeSingle<Negocio>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateNegocio(
        id: String,
        nombre: String,
        activo: Boolean
    ): Result<Negocio> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("clientes")
                    .update(
                        {
                            set("nombre_negocio", nombre)
                            set("activo", activo)
                        }
                    ) {
                        filter {
                            eq("id", id)
                        }
                    }
                    .decodeSingle<Negocio>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteNegocio(id: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {

                client
                    .from("clientes")
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
