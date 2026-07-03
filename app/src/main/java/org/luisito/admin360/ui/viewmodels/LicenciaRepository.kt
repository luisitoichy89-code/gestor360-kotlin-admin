package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.remote.SupabaseProvider
import java.time.LocalDate

/**
 * Tabla real "licencias": cliente_id uuid (FK a clientes.id), id bigint.
 * Por reglas de negocio, cada cliente/negocio debe tener una sola licencia activa
 * (no lo impone el esquema por sí solo; ver constraint UNIQUE sugerida en el chat).
 */
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
            SupabaseProvider.client
                .from("licencias")
                .insert(
                    mapOf(
                        "cliente_id" to clienteId,
                        "device_id" to deviceId,
                        "expiracion" to expiracion,
                        "activo" to true
                    )
                )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renovarLicencia(clienteId: String, dias: Int): Result<Unit> {
        return try {
            val nuevaExpiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            SupabaseProvider.client
                .from("licencias")
                .update(
                    mapOf(
                        "expiracion" to nuevaExpiracion,
                        "activo" to true
                    )
                ) {
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
