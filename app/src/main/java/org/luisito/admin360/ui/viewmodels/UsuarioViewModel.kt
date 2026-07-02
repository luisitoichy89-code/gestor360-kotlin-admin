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
            try {
                val result = repository.getUsuarios(clienteId)
                result.onSuccess { list ->
                    _uiState.value = _uiState.value.copy(isLoading = false, usuarios = list)
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
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
            try {
                repository.createUsuario(username, nombre, password, rol, clienteId, almacenId)
                loadUsuarios(clienteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
