package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String? = null,  // UUID generado por Supabase
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val almacen_id: String
)
