package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Local(
    val id: Int,
    val cliente_id: String,
    val nombre: String,
    val activo: Boolean = true,
    val created_at: String? = null
)
