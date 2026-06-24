package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AdminUser(
    val id: Int,
    val auth_id: String,
    val cliente_id: String,
    val username: String,
    val nombre: String? = null,
    val rol: String,
    val almacen_id: String,
    val activo: Boolean = true
)
