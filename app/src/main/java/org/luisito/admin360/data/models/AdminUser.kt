package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AdminUser(
    val id: Int,  // ← AHORA INT
    val auth_id: String? = null,
    val cliente_id: Int,  // ← AHORA INT
    val username: String,
    val nombre: String? = null,
    val rol: String,
    val almacen_id: Int,  // ← AHORA INT
    val activo: Boolean = true,
    val codigo_activacion: String? = null,
    val device_id_pendiente: String? = null,
    val device_approved: Boolean = false
)
