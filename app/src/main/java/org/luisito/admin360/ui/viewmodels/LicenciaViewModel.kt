package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.repository.LicenciaRepository

class LicenciaViewModel(
    private val repository: LicenciaRepository = LicenciaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LicenciaUiState())
    val uiState: StateFlow<LicenciaUiState> = _uiState.asStateFlow()

    fun loadLicencias(clienteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val licencias = repository.getLicencias(clienteId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    licencias = licencias,
                    error = if (licencias.isEmpty()) "Sin licencias" else null
                )
            }
        }
    }

    fun activateLicense(clienteId: String, deviceId: String, dias: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.activateLicense(clienteId, deviceId, dias)
            if (success) {
                loadLicencias(clienteId)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al activar licencia"
                    )
                }
            }
        }
    }

    fun renewLicense(clienteId: String, dias: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.renewLicense(clienteId, dias)
            if (success) {
                loadLicencias(clienteId)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al renovar licencia"
                    )
                }
            }
        }
    }

    fun getDiasRestantes(clienteId: String): Int {
        val licencia = _uiState.value.licencias.find { it.cliente_id == clienteId }
        return licencia?.getDiasRestantes() ?: 0
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class LicenciaUiState(
    val isLoading: Boolean = false,
    val licencias: List<Licencia> = emptyList(),
    val error: String? = null
)
