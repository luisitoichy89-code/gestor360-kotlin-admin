package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Local(
    val id: String,  // ← STRING
    val cliente_id: String,  // ← STRING
    val nombre: String,
    val activo: Boolean = true,
    val created_at: String? = null
)
