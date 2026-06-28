package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Traza

class TrazaRepository {

    suspend fun getTrazas(almacenId: String? = null): List<Traza> {
        return try {
            val query = SupabaseClientProvider.client
                .from("trazas")
                .select()
            
            if (almacenId != null && almacenId.isNotEmpty()) {
                query.eq("almacen_id", almacenId)
            }
            
            query.decodeAs<List<Traza>>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
