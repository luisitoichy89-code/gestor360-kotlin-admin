package org.luisito.admin360.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Local(
    @SerialName("id")
    val id: String = "",
    
    @SerialName("negocio_id")
    val negocio_id: String = "",
    
    @SerialName("nombre_local")
    val nombre_local: String = "",
    
    @SerialName("descripcion")
    val descripcion: String = "",
    
    @SerialName("direccion")
    val direccion: String = "",
    
    @SerialName("ciudad")
    val ciudad: String = "",
    
    @SerialName("telefono")
    val telefono: String = "",
    
    @SerialName("administrador_almacen_id")
    val administrador_almacen_id: String = "",
    
    @SerialName("cantidad_vendedores")
    val cantidad_vendedores: Int = 0,
    
    @SerialName("activo")
    val activo: Boolean = true,
    
    @SerialName("created_at")
    val created_at: String = "",
    
    @SerialName("updated_at")
    val updated_at: String = ""
)
