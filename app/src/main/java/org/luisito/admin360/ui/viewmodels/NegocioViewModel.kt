package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.data.repository.NegocioRepository

data class NegocioUiState(
    val isLoading: Boolean = false,
    val negocios: List<Negocio> = emptyList(),
    val error: String? = null
)

class NegocioViewModel(
    private val repository: NegocioRepository = NegocioRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NegocioUiState())
    val uiState: StateFlow<NegocioUiState> = _uiState.asStateFlow()
    
    fun loadNegocios(clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = repository.getNegocios(clienteId)
                result.onSuccess { list ->
                    _uiState.value = _uiState.value.copy(isLoading = false, negocios = list)
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    
    fun createNegocio(nombre: String, direccion: String, telefono: String, clienteId: String) {
        viewModelScope.launch {
            try {
                repository.createNegocio(nombre, direccion, telefono, clienteId)
                loadNegocios(clienteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
