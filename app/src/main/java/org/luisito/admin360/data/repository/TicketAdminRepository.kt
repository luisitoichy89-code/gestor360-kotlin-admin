package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.models.Ticket
import org.luisito.admin360.data.models.TicketMensaje
import org.luisito.admin360.data.remote.SupabaseProvider

class TicketAdminRepository {
    private val autor = "Admin"

    suspend fun getTodosTickets(): Result<List<Ticket>> {
        return try {
            SupabaseProvider.client.postgrest.rpc("get_todos_tickets").decodeList<Ticket>().let { Result.success(it) }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getMensajes(ticketId: Long): Result<List<TicketMensaje>> {
        return try {
            SupabaseProvider.client.postgrest
                .rpc("get_ticket_mensajes", buildJsonObject { put("p_ticket_id", ticketId) })
                .decodeList<TicketMensaje>().let { Result.success(it) }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun responderTicket(ticketId: Long, mensaje: String): Result<Unit> {
        return try {
            SupabaseProvider.client.from("ticket_mensajes").insert(mapOf(
                "ticket_id" to ticketId, "autor" to autor, "mensaje" to mensaje, "leido" to true
            ))
            SupabaseProvider.client.from("tickets").update(mapOf("updated_at" to "now()")).eq("id", ticketId)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun cambiarEstado(ticketId: Long, estado: String): Result<Unit> {
        return try {
            SupabaseProvider.client.postgrest.rpc("cambiar_estado_ticket", buildJsonObject {
                put("p_ticket_id", ticketId); put("p_estado", estado)
            })
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
