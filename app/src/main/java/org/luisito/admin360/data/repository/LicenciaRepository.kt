package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.remote.SupabaseProvider
import java.time.LocalDate

class LicenciaRepository {

    suspend fun getLicencia(clienteId: String): Result<Licencia?> {
        return try {
            val response = SupabaseProvider.client
                .from("licencias")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            Result.success(response.decodeList<Licencia>().firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activarLicencia(clienteId: String, deviceId: String, dias: Int): Result<Unit> {
        return try {
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            val payload = buildJsonObject {
                put("cliente_id", clienteId)
                put("device_id", deviceId)
                put("expiracion", expiracion)
                put("activo", true)
            }
            SupabaseProvider.client
                .from("licencias")
                .insert(payload)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renovarLicencia(clienteId: String, dias: Int): Result<Unit> {
        return try {
            val nuevaExpiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            val payload = buildJsonObject {
                put("expiracion", nuevaExpiracion)
                put("activo", true)
            }
            SupabaseProvider.client
                .from("licencias")
                .update(payload) {
                    filter {
                        eq("cliente_id", clienteId)
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
                .from("licencias")
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

    suspend fun eliminarLicencia(id: Long): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("licencias")
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
