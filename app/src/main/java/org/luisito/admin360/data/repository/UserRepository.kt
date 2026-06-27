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

    suspend fun createUser(user: User): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .insert(mapOf(
                    "username" to user.username,
                    "nombre" to user.nombre,
                    "rol" to user.rol,
                    "cliente_id" to user.cliente_id,
                    "password" to user.password,
                    "activo" to user.activo
                ))
            true
        } catch (e: Exception) { false }
    }

    suspend fun updateUser(id: String, user: User): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .update(mapOf(
                    "username" to user.username,
                    "nombre" to user.nombre,
                    "rol" to user.rol,
                    "activo" to user.activo
                )) { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteUser(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .delete { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }
}
