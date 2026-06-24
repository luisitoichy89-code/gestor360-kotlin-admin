package org.luisito.admin360.ui.screens.adminlogin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.repository.AuthRepository
import org.luisito.admin360.data.repository.LoginResult
import org.luisito.admin360.utils.DataStoreManager

class AdminLoginViewModel(
    private val context: Context,
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val dataStore = DataStoreManager(context)

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState: StateFlow<AdminLoginUiState> = _uiState.asStateFlow()

    suspend fun checkSession(): Boolean {
        return dataStore.isLoggedIn.collect { }.run { false } // Temporal
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.login(email, password)

            when (result) {
                is LoginResult.Success -> {
                    dataStore.saveSession(result.userId, email)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            userId = result.userId
                        )
                    }
                }
                is LoginResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun sendRecovery(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val success = authRepository.sendPasswordRecovery(email)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    recoverySent = success,
                    error = if (!success) "Error al enviar el correo de recuperación" else null
                )
            }
        }
    }

    fun resetState() {
        _uiState.update { AdminLoginUiState() }
    }
}

data class AdminLoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userId: String = "",
    val recoverySent: Boolean = false,
    val error: String? = null
)
