package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.util.UUID

@Serializable
data class User(
    val id: Int,
    @Contextual val auth_id: UUID? = null,
    val cliente_id: Int,
    val username: String,
    val nombre: String? = null,
    val password: String? = null,
    val rol: String,
    val almacen_id: Int,
    val activo: Boolean = true,
    val created_at: String? = null
)
