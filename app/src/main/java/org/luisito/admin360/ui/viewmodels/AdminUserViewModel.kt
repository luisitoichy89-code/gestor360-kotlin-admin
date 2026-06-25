package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.AdminUser
import org.luisito.admin360.data.repository.AdminUserRepository

data class AdminUserUiState(
    val users: List<AdminUser> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminUserViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUserUiState())
    val uiState: StateFlow<AdminUserUiState> = _uiState
    private val repo = AdminUserRepository()

    fun loadUsers(clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val users = repo.getUsers(clienteId)
                _uiState.value = _uiState.value.copy(users = users, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun createUser(clienteId: String, username: String, password: String, nombre: String, rol: String, almacenId: String, deviceId: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val ok = repo.createUser(clienteId, username, password, nombre, rol, almacenId, deviceId)
            if (ok) loadUsers(clienteId) else _uiState.value = _uiState.value.copy(error = "Error al crear usuario", isLoading = false)
        }
    }

    fun updateUser(id: Int, username: String, nombre: String, rol: String, almacenId: String, activo: Boolean, deviceId: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val ok = repo.updateUser(id, username, nombre, rol, almacenId, activo, deviceId)
            if (ok) loadUsers(username) else _uiState.value = _uiState.value.copy(error = "Error al actualizar", isLoading = false)
        }
    }

    fun deleteUser(id: Int, clienteId: String) {
        viewModelScope.launch {
            val ok = repo.deleteUser(id)
            if (ok) loadUsers(clienteId)
        }
    }

    fun confirmPasswordReset(userId: Int) {
        viewModelScope.launch { repo.confirmPasswordReset(userId) }
    }
}
