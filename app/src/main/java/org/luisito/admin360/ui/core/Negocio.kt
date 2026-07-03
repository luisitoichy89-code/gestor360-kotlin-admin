package org.luisito.admin360.data.models

import kotlinx.serialization.Serializable

/**
 * Representa una fila de la tabla "clientes" (cada cliente = un negocio que compró el APK).
 * No existe una tabla "negocios" separada; se mantiene el nombre "Negocio" en el código
 * solo por claridad de dominio en la UI del superadmin.
 */
@Serializable
data class Negocio(
    val id: String,
    val nombre_negocio: String,
    val activo: Boolean = true,
    val created_at: String? = null
)
