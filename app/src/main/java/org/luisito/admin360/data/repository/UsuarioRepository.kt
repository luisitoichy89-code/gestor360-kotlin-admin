package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.remote.SupabaseProvider

class UsuarioRepository {

    private val client = SupabaseProvider.client

    suspend fun getUsuarios(clienteId: String): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("usuarios")
                    .select {
                        filter {
                            eq("cliente_id", clienteId)
                        }
                    }
                    .decodeList<User>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createUsuario(
        username: String,
        nombre: String,
        password: String,
        rol: String,
        clienteId: String,
        almacenId: String
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("usuarios")
                    .insert(
                        mapOf(
                            "username" to username,
                            "nombre" to nombre,
                            "pin" to password,
                            "rol" to rol,
                            "cliente_id" to clienteId,
                            "almacen_id" to almacenId,
                            "activo" to true
                        )
                    )
                    .decodeSingle<User>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateUsuario(
        id: String,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        activo: Boolean
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {

                val response = client
                    .from("usuarios")
                    .update(
                        {
                            set("username", username)
                            set("nombre", nombre)
                            set("rol", rol)
                            set("almacen_id", almacenId)
                            set("activo", activo)
                        }
                    ) {
                        filter {
                            eq("id", id)
                        }
                    }
                    .decodeSingle<User>()

                Result.success(response)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteUsuario(id: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {

                client
                    .from("usuarios")
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
