package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import io.github.jan.supabase.postgrest.query.eq
import kotlinx.serialization.Serializable
import org.luisito.admin360.data.remote.SupabaseProvider

@Serializable
data class Licencia(
    val id: String? = null,
    val cliente_id: String,
    val device_id: String,
    val expiracion: String,
    val activo: Boolean = true
)

class LicenciaRepository {

    suspend fun getLicencias(clienteId: String): Result<List<Licencia>> {
        return try {
            val result = SupabaseProvider.client
                .from("licencias")
                .select {
                    filter { eq("cliente_id", clienteId) }
                }

            Result.success(result.decodeList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateLicense(clienteId: String, deviceId: String, dias: Int): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("licencias")
                .insert(
                    mapOf(
                        "cliente_id" to clienteId,
                        "device_id" to deviceId,
                        "dias" to dias,
                        "activo" to true
                    )
                )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renewLicense(clienteId: String, dias: Int): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("licencias")
                .update(
                    mapOf("dias" to dias)
                ) {
                    filter { eq("cliente_id", clienteId) }
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
                    filter { eq("id", id) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
