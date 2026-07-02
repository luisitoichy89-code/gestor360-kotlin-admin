package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Negocio(
    val id: String,
    val nombre_negocio: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val cliente_id: String? = null,
    val activo: Boolean = true,
    val created_at: String? = null
)
