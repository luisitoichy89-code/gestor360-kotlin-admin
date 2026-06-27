package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    val id: String,  // UUID en Supabase
    val auth_id: String? = null,  // UUID de auth.users
    val username: String,
    val nombre: String? = null,
    val password: String? = null,
    val rol: String,  // superadmin, admin, seller
    val cliente_id: String,  // UUID del negocio
    val activo: Boolean = true,
    val created_at: String? = null
)
