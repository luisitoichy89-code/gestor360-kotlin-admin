package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Licencia
import java.time.LocalDate

class LicenciaRepository {

    suspend fun getLicencias(clienteId: String): List<Licencia> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("licencias")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
                .decodeAs<List<Licencia>>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun activateLicense(
        clienteId: String,
        deviceId: String,
        dias: Int
    ): Boolean {
        return try {
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            val supabase = SupabaseClientProvider.client
            
            // Verificar si ya existe licencia para este negocio
            val existing = supabase.from("licencias")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
                .decodeAs<List<Licencia>>()
            
            if (existing.isNotEmpty()) {
                // Actualizar licencia existente
                supabase.from("licencias")
                    .update(
                        mapOf(
                            "device_id" to deviceId,
                            "expiracion" to expiracion,
                            "activo" to true
                        )
                    ) {
                        filter {
                            eq("cliente_id", clienteId)
                        }
                    }
            } else {
                // Crear nueva licencia
                supabase.from("licencias").insert(
                    mapOf(
                        "cliente_id" to clienteId,
                        "device_id" to deviceId,
                        "expiracion" to expiracion,
                        "activo" to true
                    )
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun renewLicense(clienteId: String, dias: Int): Boolean {
        return try {
            val expiracion = LocalDate.now().plusDays(dias.toLong()).toString()
            val supabase = SupabaseClientProvider.client
            supabase.from("licencias")
                .update(
                    mapOf(
                        "expiracion" to expiracion,
                        "activo" to true
                    )
                ) {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deactivateLicense(clienteId: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("licencias")
                .update(
                    mapOf("activo" to false)
                ) {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            true
        } catch (e: Exception) {
            false
        }
    }
}
