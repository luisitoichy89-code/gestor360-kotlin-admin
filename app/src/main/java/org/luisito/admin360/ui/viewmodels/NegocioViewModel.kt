package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.model.Negocio
import org.luisito.admin360.data.repository.NegocioRepository

data class NegocioUiState(
    val isLoading: Boolean = false,
    val negocios: List<Negocio> = emptyList(),
    val error: String? = null
)

class NegocioViewModel(
    private val repository: NegocioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NegocioUiState())
    val uiState: StateFlow<NegocioUiState> = _uiState.asStateFlow()

    init {
        loadNegocios()
    }

    fun loadNegocios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val negocios = repository.getNegocios()
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        negocios = negocios,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message 
                    ) 
                }
            }
        }
    }

    fun addNegocio(negocio: Negocio) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.addNegocio(negocio)
            if (success) {
                loadNegocios()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al agregar") }
            }
        }
    }

    fun updateNegocio(negocio: Negocio) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.updateNegocio(negocio)
            if (success) {
                loadNegocios()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al actualizar") }
            }
        }
    }

    fun deleteNegocio(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteNegocio(id)
            if (success) {
                loadNegocios()
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar") }
            }
        }
    }
}
