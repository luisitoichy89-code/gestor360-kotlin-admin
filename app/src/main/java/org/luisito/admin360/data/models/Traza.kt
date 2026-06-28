package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Traza(
    val id: String,
    val usuario: String,
    val accion: String,
    val detalle: String? = null,
    val almacen_id: String,
    val created_at: String? = null
)
