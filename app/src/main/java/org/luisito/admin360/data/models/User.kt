package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

/**
 * Coincide con la tabla real "usuarios": auth_id queda null para usuarios que solo
 * acceden por PIN (sin cuenta en Supabase Auth). El primer acceso valida contra esta
 * tabla + Android ID; después trabajan offline con caché local.
 *
 * local_id: null significa "admin con acceso a todos los locales del cliente_id".
 * Para rol "seller" siempre debe tener un local_id concreto asignado.
 */
@Serializable
data class User(
    val id: Long,
    val auth_id: String? = null,
    val cliente_id: String,
    val username: String,
    val nombre: String? = null,
    val rol: String,
    val pin: String? = null,
    val android_id: String? = null,
    val local_id: Long? = null,
    val activo: Boolean = true,
    val created_at: String? = null
)
