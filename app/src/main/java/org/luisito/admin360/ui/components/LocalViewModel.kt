package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.repository.LocalRepository

data class LocalUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val locales: List<Local> = emptyList(),
    val error: String? = null
)

class LocalViewModel(
    private val repository: LocalRepository = LocalRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocalUiState())
    val uiState: StateFlow<LocalUiState> = _uiState.asStateFlow()

    private var clienteIdActual: String? = null

    fun loadLocales(clienteId: String) {
        clienteIdActual = clienteId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getLocales(clienteId)
                .onSuccess { list -> _uiState.value = _uiState.value.copy(isLoading = false, locales = list) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Error al cargar locales") }
        }
    }

    fun refrescar() {
        clienteIdActual?.let { loadLocales(it) }
    }

    fun createLocal(nombre: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.createLocal(nombre, clienteId)
                .onSuccess { loadLocales(clienteId) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun updateLocal(id: Long, nombre: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.updateLocal(id, nombre)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun toggleActivo(local: Local) {
        viewModelScope.launch {
            repository.setActivo(local.id, !local.activo)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun deleteLocal(id: Long) {
        viewModelScope.launch {
            repository.deleteLocal(id)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
