package org.luisito.admin360.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TipoUsuario {
    SUPER_ADMIN,      // Solo yo en la app admin
    ADMIN_NEGOCIO,     // Administrador de todo el negocio (jefe)
    ADMIN_ALMACEN,     // Administrador de local/almacén
    VENDEDOR           // Vendedor en un local
}

@Serializable
data class Usuario(
    @SerialName("id")
    val id: String = "",
    
    @SerialName("android_id")
    val android_id: String = "",
    
    @SerialName("nombre")
    val nombre: String = "",
    
    @SerialName("apellido")
    val apellido: String = "",
    
    @SerialName("email")
    val email: String = "",
    
    @SerialName("telefono")
    val telefono: String = "",
    
    @SerialName("tipo_usuario")
    val tipo_usuario: String = TipoUsuario.VENDEDOR.name,  // SUPER_ADMIN, ADMIN_NEGOCIO, ADMIN_ALMACEN, VENDEDOR
    
    @SerialName("negocio_id")
    val negocio_id: String? = null,  // Null para SUPER_ADMIN
    
    @SerialName("local_id")
    val local_id: String? = null,    // Para ADMIN_ALMACEN y VENDEDOR
    
    @SerialName("activo")
    val activo: Boolean = true,
    
    @SerialName("contraseña_requerida")
    val contraseña_requerida: Boolean = true,  // Si necesita crear contraseña
    
    @SerialName("created_at")
    val created_at: String = "",
    
    @SerialName("updated_at")
    val updated_at: String = "",
    
    @SerialName("ultimo_acceso")
    val ultimo_acceso: String? = null,
    
    @SerialName("documento")
    val documento: String? = null,
    
    @SerialName("direccion")
    val direccion: String? = null
) {
    fun nombreCompleto() = "$nombre $apellido"
    fun esAdminNegocio() = tipo_usuario == TipoUsuario.ADMIN_NEGOCIO.name
    fun esAdminAlmacen() = tipo_usuario == TipoUsuario.ADMIN_ALMACEN.name
    fun esVendedor() = tipo_usuario == TipoUsuario.VENDEDOR.name
    fun esSuperAdmin() = tipo_usuario == TipoUsuario.SUPER_ADMIN.name
}
