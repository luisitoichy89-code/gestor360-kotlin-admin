package org.luisito.admin360.data.repository

private const val AUTOR_SOPORTE = "Admin"
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.models.AUTOR_SOPORTE
import org.luisito.admin360.data.models.Ticket
import org.luisito.admin360.data.models.TicketMensaje
import org.luisito.admin360.data.remote.SupabaseProvider

/**
 * Vista de superadmin: sin filtro por cliente_id, ves los tickets de TODOS
 * los negocios. Tus respuestas se insertan con leido = false a propósito,
 * para que el badge de "@soporte" se encienda en la app del negocio hasta
 * que ellos abran el ticket.
 */
class TicketAdminRepository {

    suspend fun getTickets(): Result<List<Ticket>> {
        return try {
            val tickets = SupabaseProvider.client
                .from("tickets")
                .select()
                .decodeList<Ticket>()
            Result.success(tickets.sortedByDescending { it.updated_at ?: it.created_at ?: "" })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMensajes(ticketId: Long): Result<List<TicketMensaje>> {
        return try {
            val mensajes = SupabaseProvider.client
                .from("ticket_mensajes")
                .select { filter { eq("ticket_id", ticketId) } }
                .decodeList<TicketMensaje>()
            Result.success(mensajes.sortedBy { it.created_at ?: "" })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun responder(ticketId: Long, mensaje: String): Result<Unit> {
        return try {
            val payload = buildJsonObject {
                put("ticket_id", ticketId)
                put("autor", AUTOR_SOPORTE)
                put("mensaje", mensaje)
                put("leido", false)
            }
            SupabaseProvider.client.from("ticket_mensajes").insert(payload)
            SupabaseProvider.client.from("tickets")
                .update(mapOf("estado" to "en_revision")) { filter { eq("id", ticketId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cambiarEstado(ticketId: Long, estado: String): Result<Unit> {
        return try {
            SupabaseProvider.client.from("tickets")
                .update(mapOf("estado" to estado)) { filter { eq("id", ticketId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
