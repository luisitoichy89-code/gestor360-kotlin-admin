package org.luisito.admin360.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EstadoVenta {
    PENDIENTE,
    COMPLETADA,
    CANCELADA,
    DEVUELTA
}

@Serializable
data class Venta(
    @SerialName("id")
    val id: String = "",
    
    @SerialName("local_id")
    val local_id: String = "",
    
    @SerialName("vendedor_id")
    val vendedor_id: String = "",
    
    @SerialName("numero_venta")
    val numero_venta: String = "",  // Recibo
    
    @SerialName("estado")
    val estado: String = EstadoVenta.COMPLETADA.name,
    
    @SerialName("items")
    val items: List<ItemVenta> = emptyList(),
    
    @SerialName("subtotal")
    val subtotal: Double = 0.0,
    
    @SerialName("descuento")
    val descuento: Double = 0.0,
    
    @SerialName("impuesto")
    val impuesto: Double = 0.0,
    
    @SerialName("total")
    val total: Double = 0.0,
    
    @SerialName("metodo_pago")
    val metodo_pago: String = "Efectivo",  // Efectivo, Tarjeta, Transferencia
    
    @SerialName("cliente_nombre")
    val cliente_nombre: String? = null,
    
    @SerialName("cliente_telefono")
    val cliente_telefono: String? = null,
    
    @SerialName("fecha_venta")
    val fecha_venta: String = "",
    
    @SerialName("notas")
    val notas: String? = null,
    
    @SerialName("created_at")
    val created_at: String = "",
    
    @SerialName("updated_at")
    val updated_at: String = ""
)

@Serializable
data class ItemVenta(
    @SerialName("producto_id")
    val producto_id: String = "",
    
    @SerialName("nombre_producto")
    val nombre_producto: String = "",
    
    @SerialName("cantidad")
    val cantidad: Int = 0,
    
    @SerialName("precio_unitario")
    val precio_unitario: Double = 0.0,
    
    @SerialName("subtotal")
    val subtotal: Double = 0.0
)
