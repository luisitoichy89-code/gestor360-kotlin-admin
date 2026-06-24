package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.AdminUser

class AdminUserRepository {

    suspend fun getUsers(clienteId: String): List<AdminUser> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
                .decodeAs<List<AdminUser>>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createUser(
        clienteId: String,
        username: String,
        password: String,
        nombre: String,
        rol: String,
        almacenId: String
    ): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios").insert(
                mapOf(
                    "cliente_id" to clienteId,
                    "username" to username,
                    "nombre" to nombre,
                    "rol" to rol,
                    "almacen_id" to almacenId,
                    "activo" to true
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            true
        } catch (e: Exception) {
            false
        }
    }
}
