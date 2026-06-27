package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val auth_id: String? = null,
    val username: String,
    val nombre: String? = null,
    val password: String? = null,  // Campo necesario para AuthRepository
    val rol: String,
    val cliente_id: String,
    val almacen_id: String,
    val activo: Boolean
)
