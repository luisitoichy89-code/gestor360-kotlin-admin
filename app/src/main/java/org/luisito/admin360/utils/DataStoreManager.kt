package org.luisito.admin360.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("admin_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[KEY_LOGGED_IN] ?: false }

    val userId: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[KEY_USER_ID] ?: "" }

    val userEmail: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[KEY_USER_EMAIL] ?: "" }

    suspend fun saveSession(userId: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOGGED_IN] = true
            preferences[KEY_USER_ID] = userId
            preferences[KEY_USER_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
