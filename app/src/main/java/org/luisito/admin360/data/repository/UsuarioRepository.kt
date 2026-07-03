package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.remote.SupabaseProvider

/**
 * Usuarios (admin/seller) autenticados solo por PIN + Android ID, sin cuenta en
 * Supabase Auth (auth_id queda null). El primer acceso en el dispositivo valida
 * contra esta tabla; luego la app cliente trabaja offline con caché local.
 *
 * IMPORTANTE: se usa buildJsonObject en vez de mapOf(...) porque los payloads mezclan
 * String y Boolean. Un mapOf con tipos mixtos se infiere como Map<String, Any>, y
 * kotlinx.serialization no puede serializar "Any" (error "Serializer for class 'Any'
 * is not found"). JsonObject sí tiene serializador propio y evita ese problema.
 */
class UsuarioRepository {

    suspend fun getUsuarios(clienteId: String): Result<List<User>> {
        return try {
            val response = SupabaseProvider.client
                .from("usuarios")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            Result.success(response.decodeList<User>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NOTA DE SEGURIDAD: idealmente esto debería crear el usuario vía Supabase Auth
    // (auth.admin.createUser) y guardar solo una referencia (auth_id) en "usuarios",
    // en vez de escribir la contraseña en texto plano en la tabla. Se deja el insert
    // directo para no romper el esquema actual, pero es la primera deuda técnica a
    // resolver antes de producción.
    suspend fun createUsuario(
        username: String,
        nombre: String,
        pin: String,
        rol: String,
        clienteId: String,
        almacenId: String,
        androidId: String
    ): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("username", username)
                put("nombre", nombre)
                put("pin", pin)
                put("rol", rol)
                put("cliente_id", clienteId)
                put("almacen_id", almacenId)
                put("android_id", androidId)
                put("activo", true)
            }
            SupabaseProvider.client
                .from("usuarios")
                .insert(payload)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUsuario(
        id: Long,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        androidId: String,
        activo: Boolean
    ): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("username", username)
                put("nombre", nombre)
                put("rol", rol)
                put("almacen_id", almacenId)
                put("android_id", androidId)
                put("activo", activo)
            }
            SupabaseProvider.client
                .from("usuarios")
                .update(payload) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cambiar el PIN es una acción separada del resto de la edición, por seguridad. */
    suspend fun cambiarPin(id: Long, nuevoPin: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .update(mapOf("pin" to nuevoPin)) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setActivo(id: Long, activo: Boolean): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
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

    suspend fun deleteUsuario(id: Long): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
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
