package org.luisito.admin360.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Negocio(
    @SerialName("id")
    val id: String = "",
    
    @SerialName("nombre_negocio")
    val nombre_negocio: String = "",
    
    @SerialName("descripcion")
    val descripcion: String = "",
    
    @SerialName("propietario_id")
    val propietario_id: String = "",
    
    @SerialName("activo")
    val activo: Boolean = true,
    
    @SerialName("cantidad_locales")
    val cantidad_locales: Int = 0,
    
    @SerialName("created_at")
    val created_at: String = "",
    
    @SerialName("updated_at")
    val updated_at: String = "",
    
    @SerialName("telefono")
    val telefono: String? = null,
    
    @SerialName("email")
    val email: String? = null,
    
    @SerialName("direccion")
    val direccion: String? = null,
    
    @SerialName("ciudad")
    val ciudad: String? = null,
    
    @SerialName("pais")
    val pais: String? = null,
    
    @SerialName("rut")
    val rut: String? = null,
    
    @SerialName("razon_social")
    val razon_social: String? = null
) {
    fun isEmpty() = nombre_negocio.isBlank()
}
