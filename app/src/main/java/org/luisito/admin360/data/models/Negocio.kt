package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Negocio(
    val id: Int,
    val nombre_negocio: String,
    val ruc: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val email: String? = null,
    val activo: Boolean = true,
    val created_at: String? = null
)
