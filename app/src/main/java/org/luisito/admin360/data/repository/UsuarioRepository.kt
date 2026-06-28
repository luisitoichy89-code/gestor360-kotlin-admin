package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.User
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.MessageDigest

class UsuarioRepository {

    suspend fun getUsuarios(clienteId: String): List<User> {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("usuarios")
                .select(Columns.ALL) {
                    filter { eq("cliente_id", clienteId) }
                }
                .decodeAs<List<User>>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUsuariosByLocal(almacenId: String): List<User> {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("usuarios")
                .select(Columns.ALL) {
                    filter { eq("almacen_id", almacenId) }
                }
                .decodeAs<List<User>>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createUsuario(
        username: String,
        nombre: String,
        password: String,
        rol: String,
        clienteId: String,
        almacenId: String
    ): Boolean {
        return try {
            val hashedPassword = hash(password)
            val data = buildJsonObject {
                put("username", username)
                put("nombre", nombre)
                put("password", hashedPassword)
                put("rol", rol)
                put("cliente_id", clienteId)
                put("almacen_id", almacenId)
                put("activo", true)
            }
            SupabaseClientProvider.client
                .postgrest.from("usuarios")
                .insert(data) {
                    select(Columns.ALL)
                }
                .decodeAs<List<User>>()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateUsuario(
        id: String,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        activo: Boolean
    ): Boolean {
        return try {
            val data = buildJsonObject {
                put("username", username)
                put("nombre", nombre)
                put("rol", rol)
                put("almacen_id", almacenId)
                put("activo", activo)
            }
            SupabaseClientProvider.client
                .postgrest.from("usuarios")
                .update(data) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun resetPassword(id: String): Boolean {
        return try {
            val defaultPassword = hash("123456")
            val data = buildJsonObject {
                put("password", defaultPassword)
            }
            SupabaseClientProvider.client
                .postgrest.from("usuarios")
                .update(data) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteUsuario(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("usuarios")
                .delete {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
