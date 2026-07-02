package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Local(
    val id: Int,
    val negocio_id: String,
    val nombre: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val activo: Boolean = true,
    val created_at: String? = null
)
