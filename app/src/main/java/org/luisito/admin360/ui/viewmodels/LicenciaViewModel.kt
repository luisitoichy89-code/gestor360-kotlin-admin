package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.repository.LicenciaRepository

data class LicenciaUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val licencia: Licencia? = null,
    val error: String? = null
)

class LicenciaViewModel(
    private val repository: LicenciaRepository = LicenciaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LicenciaUiState())
    val uiState: StateFlow<LicenciaUiState> = _uiState.asStateFlow()

    private var clienteIdActual: String? = null

    fun loadLicencia(clienteId: String) {
        clienteIdActual = clienteId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getLicencia(clienteId)
                .onSuccess { licencia -> _uiState.value = _uiState.value.copy(isLoading = false, licencia = licencia) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Error al cargar la licencia") }
        }
    }

    fun refrescar() {
        clienteIdActual?.let { loadLicencia(it) }
    }

    fun activarLicencia(deviceId: String, dias: Int) {
        val clienteId = clienteIdActual ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.activarLicencia(clienteId, deviceId, dias)
                .onSuccess { loadLicencia(clienteId) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun renovarLicencia(dias: Int) {
        val clienteId = clienteIdActual ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.renovarLicencia(clienteId, dias)
                .onSuccess { loadLicencia(clienteId) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun toggleActivo() {
        val licencia = _uiState.value.licencia ?: return
        val id = licencia.id ?: return
        viewModelScope.launch {
            repository.setActivo(id, !licencia.activo)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun eliminarLicencia() {
        val id = _uiState.value.licencia?.id ?: return
        viewModelScope.launch {
            repository.eliminarLicencia(id)
                .onSuccess { refrescar() }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
