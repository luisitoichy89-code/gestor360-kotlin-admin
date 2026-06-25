package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.repository.LocalRepository

data class LocalUiState(
    val locales: List<Local> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LocalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LocalUiState())
    val uiState: StateFlow<LocalUiState> = _uiState
    private val repo = LocalRepository()

    fun loadLocales(clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val locales = repo.getLocales(clienteId)
                _uiState.value = _uiState.value.copy(locales = locales, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun createLocal(clienteId: String, nombre: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val ok = repo.createLocal(clienteId, nombre)
            if (ok) loadLocales(clienteId) else _uiState.value = _uiState.value.copy(error = "Error al crear local", isLoading = false)
        }
    }

    fun updateLocal(id: Int, nombre: String, activo: Boolean, clienteId: String) {
        viewModelScope.launch {
            val ok = repo.updateLocal(id, nombre, activo)
            if (ok) loadLocales(clienteId)
        }
    }

    fun deleteLocal(id: Int, clienteId: String) {
        viewModelScope.launch {
            val ok = repo.deleteLocal(id)
            if (ok) loadLocales(clienteId)
        }
    }
}
