package org.luisito.admin360.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("admin_prefs", Context.MODE_PRIVATE)

    fun saveLogin(userId: String) {
        prefs.edit().putBoolean("is_logged_in", true).putString("user_id", userId).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getUserId(): String {
        return prefs.getString("user_id", "") ?: ""
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
