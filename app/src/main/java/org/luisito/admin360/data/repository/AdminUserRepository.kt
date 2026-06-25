package org.luisito.admin360.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider

class AdminUserRepository {
    suspend fun getUsers(clienteId: String): List<Map<String, Any>> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios").select { filter { eq("cliente_id", clienteId) } }.decodeAs<List<Map<String, Any>>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createUser(clienteId: String, username: String, password: String, nombre: String, rol: String, almacenId: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            val email = "$username@gestor360.local"
            val authRes = supabase.auth.signUpWithEmail(email, password)
            val authId = authRes.user?.id ?: return false
            supabase.from("usuarios").insert(mapOf(
                "auth_id" to authId, "cliente_id" to clienteId, "username" to username,
                "nombre" to nombre, "rol" to rol, "almacen_id" to almacenId, "activo" to true
            ))
            true
        } catch (e: Exception) { false }
    }
}
