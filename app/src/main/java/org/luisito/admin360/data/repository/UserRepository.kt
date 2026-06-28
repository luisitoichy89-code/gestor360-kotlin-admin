package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.User

class UserRepository {

    suspend fun getUsers(clienteId: String): List<User> {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .select { filter { eq("cliente_id", clienteId) } }
                .decodeAs<List<User>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createUser(
        clienteId: String,
        almacenId: String,
        username: String,
        nombre: String?,
        email: String?,
        telefono: String?,
        password: String,
        rol: String
    ): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .insert(mapOf(
                    "cliente_id" to clienteId,
                    "almacen_id" to almacenId,
                    "username" to username,
                    "nombre" to nombre,
                    "email" to email,
                    "telefono" to telefono,
                    "password" to password,
                    "rol" to rol,
                    "activo" to true
                )) {
                    select()
                }
            true
        } catch (e: Exception) { false }
    }

    suspend fun updateUser(
        id: String,
        username: String,
        nombre: String?,
        email: String?,
        telefono: String?,
        rol: String,
        activo: Boolean
    ): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .update(mapOf(
                    "username" to username,
                    "nombre" to nombre,
                    "email" to email,
                    "telefono" to telefono,
                    "rol" to rol,
                    "activo" to activo
                )) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteUser(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .delete {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) { false }
    }
}
