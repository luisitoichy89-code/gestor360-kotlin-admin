package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Negocio(
    val id: Int,  // ← AHORA INT
    val nombre_negocio: String,
    val activo: Boolean = true,
    val created_at: String? = null
)
