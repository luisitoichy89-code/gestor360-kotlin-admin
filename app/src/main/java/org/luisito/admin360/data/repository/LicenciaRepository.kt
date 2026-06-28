package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Licencia
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate

class LicenciaRepository {

    suspend fun getLicencias(clienteId: String): List<Licencia> {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("licencias")
                .select(Columns.ALL) {
                    filter { eq("cliente_id", clienteId) }
                }
                .decodeAs<List<Licencia>>()
        } catch (e: Exception) { 
            e.printStackTrace()
            emptyList() 
        }
    }

    suspend fun activateLicense(clienteId: String, deviceId: String, dias: Int): Boolean {
        return try {
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            val data = buildJsonObject {
                put("cliente_id", clienteId)
                put("device_id", deviceId)
                put("expiracion", expiracion)
                put("activo", true)
            }
            SupabaseClientProvider.client
                .postgrest.from("licencias")
                .insert(data) {
                    select(Columns.ALL)
                }
                .decodeAs<List<Licencia>>()
            true
        } catch (e: Exception) { 
            e.printStackTrace()
            false 
        }
    }

    suspend fun renewLicense(clienteId: String, dias: Int): Boolean {
        return try {
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            val data = buildJsonObject {
                put("expiracion", expiracion)
                put("activo", true)
            }
            SupabaseClientProvider.client
                .postgrest.from("licencias")
                .update(data) {
                    filter { eq("cliente_id", clienteId) }
                }
            true
        } catch (e: Exception) { 
            e.printStackTrace()
            false 
        }
    }

    suspend fun deleteLicense(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .postgrest.from("licencias")
                .delete {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) { 
            e.printStackTrace()
            false 
        }
    }
}
