package org.luisito.admin360.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import org.luisito.admin360.data.models.AdminUser

@Database(entities = [AdminUser::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adminUserDao(): AdminUserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestor360_admin.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
