package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int = 0,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val almacen_id: String
)
