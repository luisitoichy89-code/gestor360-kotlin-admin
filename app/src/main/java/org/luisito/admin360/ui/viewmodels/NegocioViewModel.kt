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

    fun loadNegocios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.getNegocios()

            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    negocios = data,
                    error = null
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun createNegocio(nombre: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.createNegocio(nombre)

            result.onSuccess {
                loadNegocios()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateNegocio(id: String, nombre: String, activo: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.updateNegocio(id, nombre, activo)

            result.onSuccess {
                loadNegocios()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteNegocio(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.deleteNegocio(id)

            result.onSuccess {
                loadNegocios()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
