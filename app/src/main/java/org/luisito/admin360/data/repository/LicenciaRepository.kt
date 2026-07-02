package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.eq
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.remote.SupabaseProvider
import java.time.LocalDate

class LicenciaRepository {

    suspend fun getLicencias(clienteId: String): Result<List<Licencia>> {
        return try {
            val response = SupabaseProvider.client
                .from("licencias")
                .select {
                    eq("cliente_id", clienteId)
                }
            Result.success(response.decodeList<Licencia>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateLicense(
        clienteId: String,
        deviceId: String,
        dias: Int
    ): Result<Unit> {
        return try {
            val expiracion = LocalDate.now()
                .plusDays(dias.toLong())
                .toString()

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

    suspend fun renewLicense(
        clienteId: String,
        dias: Int
    ): Result<Unit> {
        return try {
            val nuevaExpiracion = LocalDate.now()
                .plusDays(dias.toLong())
                .toString()

            SupabaseProvider.client
                .from("licencias")
                .update(
                    mapOf(
                        "expiracion" to nuevaExpiracion
                    )
                ) {
                    eq("cliente_id", clienteId)
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLicense(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("licencias")
                .delete {
                    eq("id", id)
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
