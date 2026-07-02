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
    val isSaving: Boolean = false,
    val negocios: List<Negocio> = emptyList(),
    val error: String? = null
)

class NegocioViewModel(
    private val repository: NegocioRepository = NegocioRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NegocioUiState())
    val uiState: StateFlow<NegocioUiState> = _uiState.asStateFlow()

    private var clienteIdActual: String? = null

    fun loadNegocios(clienteId: String) {
        clienteIdActual = clienteId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getNegocios(clienteId)
                .onSuccess { list -> _uiState.value = _uiState.value.copy(isLoading = false, negocios = list) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Error al cargar negocios") }
        }
    }

    fun refrescar() {
        clienteIdActual?.let { loadNegocios(it) }
    }

    fun createNegocio(nombre: String, direccion: String, telefono: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.createNegocio(nombre, direccion, telefono, clienteId)
                .onSuccess { loadNegocios(clienteId) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun updateNegocio(id: String, nombre: String, direccion: String, telefono: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.updateNegocio(id, nombre, direccion, telefono)
                .onSuccess { clienteIdActual?.let { loadNegocios(it) } }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun toggleActivo(negocio: Negocio) {
        viewModelScope.launch {
            repository.setActivo(negocio.id, !negocio.activo)
                .onSuccess { clienteIdActual?.let { loadNegocios(it) } }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun deleteNegocio(id: String) {
        viewModelScope.launch {
            repository.deleteNegocio(id)
                .onSuccess { clienteIdActual?.let { loadNegocios(it) } }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
