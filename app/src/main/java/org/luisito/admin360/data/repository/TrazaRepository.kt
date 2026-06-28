package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Traza

class TrazaRepository {

    suspend fun getTrazas(almacenId: String? = null): List<Traza> {
        return try {
            SupabaseClientProvider.client
                .from("trazas")
                .select()
                .decodeAs<List<Traza>>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
