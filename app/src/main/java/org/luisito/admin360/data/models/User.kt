package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val auth_id: String? = null,
    val cliente_id: String,
    val almacen_id: String,
    val username: String,
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val password: String? = null,
    val rol: String,
    val activo: Boolean = true,
    val created_at: String? = null
)
