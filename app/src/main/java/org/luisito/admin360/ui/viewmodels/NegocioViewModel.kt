package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.data.repository.ErrorHolder
import org.luisito.admin360.data.repository.NegocioRepository

class NegocioViewModel(
    private val repository: NegocioRepository = NegocioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NegocioUiState())
    val uiState: StateFlow<NegocioUiState> = _uiState.asStateFlow()

    fun loadNegocios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val negocios = repository.getNegocios()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    negocios = negocios,
                    error = if (negocios.isEmpty()) "No hay negocios" else null
                )
            }
        }
    }

    fun createNegocio(nombre: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.createNegocio(nombre)
            if (success) {
                loadNegocios()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = ErrorHolder.lastError
                    )
                }
            }
        }
    }

    fun updateNegocio(id: Int, nombre: String, activo: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.updateNegocio(id, nombre, activo)
            if (success) {
                loadNegocios()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al actualizar negocio"
                    )
                }
            }
        }
    }

    fun deleteNegocio(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteNegocio(id)
            if (success) {
                loadNegocios()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al eliminar negocio"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class NegocioUiState(
    val isLoading: Boolean = false,
    val negocios: List<Negocio> = emptyList(),
    val error: String? = null
)
