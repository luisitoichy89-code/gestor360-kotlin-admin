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
    val isSaving: Boolean = false,
    val usuarios: List<User> = emptyList(),
    val error: String? = null
)

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuarioUiState())
    val uiState: StateFlow<UsuarioUiState> = _uiState.asStateFlow()

    private var clienteIdActual: String? = null

    fun loadUsuarios(clienteId: String) {
        clienteIdActual = clienteId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getUsuarios(clienteId)
                .onSuccess { list -> _uiState.value = _uiState.value.copy(isLoading = false, usuarios = list) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Error al cargar usuarios") }
        }
    }

    fun refrescar() {
        clienteIdActual?.let { loadUsuarios(it) }
    }

    /**
     * localId llega como String desde la UI (el selector de local trabaja con
     * "l.id.toString()") y se convierte acá a Long?. rol == "admin" siempre
     * guarda null (acceso a todos los locales del negocio); rol == "seller"
     * requiere un local concreto.
     */
    fun createUsuario(
        username: String,
        nombre: String,
        pin: String,
        rol: String,
        clienteId: String,
        almacenId: String,
        androidId: String
    ) {
        val localId = if (rol == "admin") null else almacenId.toLongOrNull()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.createUsuario(username, nombre, pin, rol, clienteId, localId, androidId)
                .onSuccess { loadUsuarios(clienteId) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun updateUsuario(
        id: Long,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        androidId: String,
        activo: Boolean
    ) {
        val localId = if (rol == "admin") null else almacenId.toLongOrNull()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.updateUsuario(id, username, nombre, rol, localId, androidId, activo)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun cambiarPin(id: Long, nuevoPin: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.cambiarPin(id, nuevoPin)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun toggleActivo(user: User) {
        viewModelScope.launch {
            repository.setActivo(user.id, !user.activo)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun deleteUsuario(id: Long) {
        viewModelScope.launch {
            repository.deleteUsuario(id)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
