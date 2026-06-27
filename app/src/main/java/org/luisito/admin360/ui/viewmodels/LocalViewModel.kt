package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.repository.LocalRepository

data class LocalUiState(
    val isLoading: Boolean = false,
    val locales: List<Local> = emptyList(),
    val error: String? = null
)

class LocalViewModel(
    private val repository: LocalRepository = LocalRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocalUiState())
    val uiState: StateFlow<LocalUiState> = _uiState.asStateFlow()

    fun loadLocales(clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val locales = repository.getLocales(clienteId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    locales = locales,
                    error = if (locales.isEmpty()) "No hay locales" else null
                )
            }
        }
    }

    fun createLocal(clienteId: String, nombre: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.createLocal(clienteId, nombre)
            if (success) {
                loadLocales(clienteId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al crear local") }
            }
        }
    }

    fun updateLocal(id: String, nombre: String, activo: Boolean, clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.updateLocal(id, nombre, activo)
            if (success) {
                loadLocales(clienteId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al actualizar") }
            }
        }
    }

    fun deleteLocal(id: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteLocal(id)
            if (success) {
                loadLocales(clienteId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar") }
            }
        }
    }
}
