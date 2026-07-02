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
    val licencias: List<Licencia> = emptyList(),
    val error: String? = null
)

class LicenciaViewModel(
    private val repository: LicenciaRepository = LicenciaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LicenciaUiState())
    val uiState: StateFlow<LicenciaUiState> = _uiState.asStateFlow()

    private var clienteIdActual: String? = null

    fun loadLicencias(clienteId: String) {
        clienteIdActual = clienteId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.getLicencias(clienteId)
                .onSuccess { list -> _uiState.value = _uiState.value.copy(isLoading = false, licencias = list) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Error al cargar licencias") }
        }
    }

    fun refrescar() {
        clienteIdActual?.let { loadLicencias(it) }
    }

    fun activateLicense(clienteId: String, deviceId: String, dias: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.activateLicense(clienteId, deviceId, dias)
                .onSuccess { loadLicencias(clienteId) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun renewLicense(clienteId: String, dias: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            repository.renewLicense(clienteId, dias)
                .onSuccess { loadLicencias(clienteId) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    fun toggleActivo(licencia: Licencia) {
        val id = licencia.id ?: return
        viewModelScope.launch {
            repository.setActivo(id, !licencia.activo)
                .onSuccess { clienteIdActual?.let { loadLicencias(it) } }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun deleteLicense(id: String) {
        viewModelScope.launch {
            repository.deleteLicense(id)
                .onSuccess { clienteIdActual?.let { loadLicencias(it) } }
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
