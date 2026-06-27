package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Local(
    val id: Int,  // ← AHORA INT
    val cliente_id: Int,  // ← AHORA INT
    val nombre: String,
    val activo: Boolean = true,
    val created_at: String? = null
)
