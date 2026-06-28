package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.repository.UserRepository
import java.security.MessageDigest

data class UserUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)

class UserViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun loadUsers(clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val users = repository.getUsers(clienteId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    users = users,
                    error = if (users.isEmpty()) "No hay usuarios" else null
                )
            }
        }
    }

    fun createUser(
        clienteId: String,
        almacenId: String,
        username: String,
        nombre: String?,
        email: String?,
        telefono: String?,
        password: String,
        rol: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val hashedPassword = hash(password)
            val success = repository.createUser(
                clienteId, almacenId, username, nombre, email, telefono, hashedPassword, rol
            )
            if (success) {
                loadUsers(clienteId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al crear usuario") }
            }
        }
    }

    fun updateUser(
        id: String,
        username: String,
        nombre: String?,
        email: String?,
        telefono: String?,
        rol: String,
        activo: Boolean,
        clienteId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.updateUser(id, username, nombre, email, telefono, rol, activo)
            if (success) {
                loadUsers(clienteId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al actualizar") }
            }
        }
    }

    fun deleteUser(id: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteUser(id)
            if (success) {
                loadUsers(clienteId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar") }
            }
        }
    }

    private fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
