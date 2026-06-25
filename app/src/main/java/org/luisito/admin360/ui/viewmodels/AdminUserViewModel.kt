package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.AdminUser
import org.luisito.admin360.data.repository.AdminUserRepository
import kotlin.random.Random

class AdminUserViewModel(
    private val repository: AdminUserRepository = AdminUserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUserUiState())
    val uiState: StateFlow<AdminUserUiState> = _uiState.asStateFlow()

    fun generateCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

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
        username: String,
        password: String,
        nombre: String,
        rol: String,
        almacenId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val codigo = generateCode()
            val success = repository.createUser(clienteId, username, password, nombre, rol, almacenId, codigo)
            if (success) {
                loadUsers(clienteId)
                _uiState.update { it.copy(codigoGenerado = codigo) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al crear usuario"
                    )
                }
            }
        }
    }

    fun updateUser(
        id: Int,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        activo: Boolean
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.updateUser(id, username, nombre, rol, almacenId, activo)
            if (success) {
                val clienteId = _uiState.value.users.firstOrNull()?.cliente_id ?: ""
                loadUsers(clienteId)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al actualizar usuario"
                    )
                }
            }
        }
    }

    fun deleteUser(id: Int, clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteUser(id)
            if (success) {
                loadUsers(clienteId)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al eliminar usuario"
                    )
                }
            }
        }
    }

    fun approveUser(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.approveUser(id)
            if (success) {
                val clienteId = _uiState.value.users.firstOrNull()?.cliente_id ?: ""
                loadUsers(clienteId)
                _uiState.update { it.copy(usuarioAprobado = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al aprobar usuario"
                    )
                }
            }
        }
    }

    fun clearEstado() {
        _uiState.update { it.copy(codigoGenerado = null, usuarioAprobado = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class AdminUserUiState(
    val isLoading: Boolean = false,
    val users: List<AdminUser> = emptyList(),
    val error: String? = null,
    val codigoGenerado: String? = null,
    val usuarioAprobado: Boolean = false
)
