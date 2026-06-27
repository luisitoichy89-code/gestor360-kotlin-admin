package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,  // ← STRING
    val auth_id: String? = null,
    val username: String,
    val nombre: String? = null,
    val password: String? = null,
    val rol: String,
    val cliente_id: String,  // ← STRING
    val almacen_id: String,  // ← STRING
    val activo: Boolean = true,
    val created_at: String? = null
)
