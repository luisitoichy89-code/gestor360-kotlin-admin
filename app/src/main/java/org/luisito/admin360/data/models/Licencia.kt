package org.luisito.admin360.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EstadoLicencia {
    ACTIVA,
    INACTIVA,
    EXPIRADA,
    CANCELADA
}

@Serializable
data class Licencia(
    @SerialName("id")
    val id: String = "",
    
    @SerialName("negocio_id")
    val negocio_id: String = "",
    
    @SerialName("android_id")
    val android_id: String = "",  // ID del dispositivo (admin almacén)
    
    @SerialName("admin_negocio_id")
    val admin_negocio_id: String = "",  // Usuario admin del negocio que compró
    
    @SerialName("estado")
    val estado: String = EstadoLicencia.ACTIVA.name,
    
    @SerialName("fecha_inicio")
    val fecha_inicio: String = "",
    
    @SerialName("fecha_vencimiento")
    val fecha_vencimiento: String = "",
    
    @SerialName("dias_pagados")
    val dias_pagados: Int = 0,
    
    @SerialName("dias_restantes")
    val dias_restantes: Int = 0,
    
    @SerialName("precio_pagado")
    val precio_pagado: Double = 0.0,
    
    @SerialName("moneda")
    val moneda: String = "COP",
    
    @SerialName("metodo_pago")
    val metodo_pago: String = "",  // Transferencia, Tarjeta, Efectivo
    
    @SerialName("referencia_pago")
    val referencia_pago: String = "",  // Número de transferencia o referencia
    
    @SerialName("created_at")
    val created_at: String = "",
    
    @SerialName("updated_at")
    val updated_at: String = "",
    
    @SerialName("notas")
    val notas: String? = null
) {
    fun esActiva() = estado == EstadoLicencia.ACTIVA.name && dias_restantes > 0
    fun estaPorVencer() = dias_restantes in 1..7
    fun estaExpirada() = dias_restantes <= 0
}
