package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.getUsuarios(clienteId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    usuarios = it,
                    error = null
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
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
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.createUsuario(
                username,
                nombre,
                password,
                rol,
                clienteId,
                almacenId
            )

            result.onSuccess {
                loadUsuarios(clienteId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
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
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.updateUsuario(
                id,
                username,
                nombre,
                rol,
                almacenId,
                activo
            )

            result.onSuccess {
                loadUsuarios(clienteId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }

    fun deleteUsuario(id: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.deleteUsuario(id)

            result.onSuccess {
                loadUsuarios(clienteId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }
}
