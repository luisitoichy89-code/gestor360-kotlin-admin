package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.models.Ticket
import org.luisito.admin360.data.models.TicketMensaje
import org.luisito.admin360.data.remote.SupabaseProvider

class TicketRepository {
    suspend fun getTodosTickets(): Result<List<Ticket>> {
        return try {
            val tickets = SupabaseProvider.client.postgrest.rpc("get_todos_tickets").decodeList<Ticket>()
            Result.success(tickets)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getMensajes(ticketId: Long): Result<List<TicketMensaje>> {
        return try {
            val mensajes = SupabaseProvider.client.postgrest
                .rpc("get_ticket_mensajes", buildJsonObject { put("p_ticket_id", ticketId) })
                .decodeList<TicketMensaje>()
            Result.success(mensajes)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun responderTicket(nombre: String, ticketId: Long, mensaje: String): Result<Unit> {
        return try {
            // Para admin usamos responder_ticket con android_id fijo "admin"
            SupabaseProvider.client.from("ticket_mensajes").insert(mapOf(
                "ticket_id" to ticketId, "autor" to "Admin", "mensaje" to mensaje
            ))
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
