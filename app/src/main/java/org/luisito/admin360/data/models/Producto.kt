package org.luisito.admin360.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    @SerialName("id")
    val id: String = "",
    
    @SerialName("local_id")
    val local_id: String = "",
    
    @SerialName("nombre")
    val nombre: String = "",
    
    @SerialName("descripcion")
    val descripcion: String = "",
    
    @SerialName("codigo_barras")
    val codigo_barras: String? = null,
    
    @SerialName("sku")
    val sku: String? = null,
    
    @SerialName("precio_compra")
    val precio_compra: Double = 0.0,
    
    @SerialName("precio_venta")
    val precio_venta: Double = 0.0,
    
    @SerialName("cantidad_stock")
    val cantidad_stock: Int = 0,
    
    @SerialName("cantidad_minima")
    val cantidad_minima: Int = 10,
    
    @SerialName("categoria")
    val categoria: String = "",
    
    @SerialName("activo")
    val activo: Boolean = true,
    
    @SerialName("created_at")
    val created_at: String = "",
    
    @SerialName("updated_at")
    val updated_at: String = ""
) {
    fun gananciaPorUnidad() = precio_venta - precio_compra
    fun gananciaTotal() = gananciaPorUnidad() * cantidad_stock
    fun necesitaReorden() = cantidad_stock <= cantidad_minima
}
