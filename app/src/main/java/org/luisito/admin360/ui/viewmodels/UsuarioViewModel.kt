package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.repository.UsuarioRepository

data class UsuarioUiState(
    val isLoading: Boolean = false,
    val usuarios: List<User> = emptyList(),
    val error: String? = null
)

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    fun loadUsuarios(clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val usuarios = repository.getUsuarios(clienteId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    usuarios = usuarios,
                    error = if (usuarios.isEmpty()) "No hay usuarios" else null
                )
            }
        }
    }

    fun loadUsuariosByLocal(almacenId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val usuarios = repository.getUsuariosByLocal(almacenId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    usuarios = usuarios,
                    error = if (usuarios.isEmpty()) "No hay usuarios en este local" else null
                )
            }
        }
    }

    fun createUsuario(
        username: String,
        nombre: String,
        password: String,
        rol: String,
        clienteId: String,
        almacenId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.createUsuario(username, nombre, password, rol, clienteId, almacenId)
            if (success) {
                loadUsuarios(clienteId)
            } else {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Error al crear usuario") 
                }
            }
        }
    }

    fun updateUsuario(
        id: String,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        activo: Boolean,
        clienteId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.updateUsuario(id, username, nombre, rol, almacenId, activo)
            if (success) {
                loadUsuarios(clienteId)
            } else {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Error al actualizar usuario") 
                }
            }
        }
    }

    fun resetPassword(id: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.resetPassword(id)
            if (success) {
                loadUsuarios(clienteId)
            } else {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Error al restablecer contraseña") 
                }
            }
        }
    }

    fun deleteUsuario(id: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteUsuario(id)
            if (success) {
                loadUsuarios(clienteId)
            } else {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Error al eliminar usuario") 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
