package org.luisito.admin360.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.luisito.admin360.data.models.AdminUser

@Dao
interface AdminUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: AdminUser)

    @Query("SELECT * FROM admin_users WHERE cliente_id = :clienteId")
    suspend fun getUsersByCliente(clienteId: String): List<AdminUser>

    @Query("UPDATE admin_users SET device_approved = 1 WHERE id = :userId")
    suspend fun approveUser(userId: Int)

    @Query("DELETE FROM admin_users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}
