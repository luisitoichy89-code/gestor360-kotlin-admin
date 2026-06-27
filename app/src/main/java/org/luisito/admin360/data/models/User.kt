package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,  // ← AHORA INT
    val auth_id: String? = null,
    val username: String,
    val nombre: String? = null,
    val password: String? = null,
    val rol: String,  // superadmin, admin_almacen, seller
    val cliente_id: Int,  // ← AHORA INT
    val almacen_id: Int,  // ← AHORA INT (local asignado)
    val activo: Boolean = true,
    val created_at: String? = null
)
