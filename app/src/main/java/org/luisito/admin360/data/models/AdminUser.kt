package org.luisito.admin360.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "admin_users")
data class AdminUser(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "auth_id")
    val auth_id: String? = null,
    @ColumnInfo(name = "cliente_id")
    val cliente_id: String,
    val username: String,
    val nombre: String? = null,
    val rol: String,
    @ColumnInfo(name = "almacen_id")
    val almacen_id: String,
    val activo: Boolean = true,
    @ColumnInfo(name = "codigo_activacion")
    val codigo_activacion: String? = null,
    @ColumnInfo(name = "device_id_pendiente")
    val device_id_pendiente: String? = null,
    @ColumnInfo(name = "device_approved")
    val device_approved: Boolean = false
)
