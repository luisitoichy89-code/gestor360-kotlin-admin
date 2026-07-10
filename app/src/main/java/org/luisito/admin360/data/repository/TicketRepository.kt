package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.models.Ticket
import org.luisito.admin360.data.models.TicketMensaje
import org.luisito.admin360.data.remote.SupabaseProvider

/**
 * Consola de soporte del super-admin (dueño del SaaS), NO de un negocio en
 * particular: ve tickets de TODOS los clientes/negocios. Por eso esto no usa
 * p_android_id/p_local_id como la app cliente — usa la sesión real de
 * Supabase Auth (JWT) con la que este admin inició sesión (ver AuthRepository).
 * Antes esto mandaba p_android_id = "admin" (un string fijo, sin validar
 * nada); ahora las funciones "admin_*" validan auth.uid() contra la tabla
 * "admins" del lado del servidor.
 */
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
                .rpc("admin_get_ticket_mensajes", buildJsonObject { put("p_ticket_id", ticketId) })
                .decodeList<TicketMensaje>()
            Result.success(mensajes)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun responderTicket(ticketId: Long, mensaje: String): Result<Unit> {
        return try {
            SupabaseProvider.client.postgrest.rpc("admin_responder_ticket", buildJsonObject {
                put("p_ticket_id", ticketId); put("p_mensaje", mensaje)
            })
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun cambiarEstado(ticketId: Long, estado: String): Result<Unit> {
        return try {
            SupabaseProvider.client.postgrest.rpc("admin_cambiar_estado_ticket", buildJsonObject {
                put("p_ticket_id", ticketId); put("p_estado", estado)
            })
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
