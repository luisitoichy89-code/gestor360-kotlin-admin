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
    val licencias: List<Licencia> = emptyList(),
    val error: String? = null
)

class LicenciaViewModel(
    private val repository: LicenciaRepository = LicenciaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LicenciaUiState())
    val uiState: StateFlow<LicenciaUiState> = _uiState.asStateFlow()

    fun loadLicencias(clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = repository.getLicencias(clienteId)
                result.onSuccess { list ->
                    _uiState.value = _uiState.value.copy(isLoading = false, licencias = list)
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun activateLicense(clienteId: String, deviceId: String, dias: Int) {
        viewModelScope.launch {
            try {
                repository.activateLicense(clienteId, deviceId, dias)
                loadLicencias(clienteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun renewLicense(clienteId: String, dias: Int) {
        viewModelScope.launch {
            try {
                repository.renewLicense(clienteId, dias)
                loadLicencias(clienteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteLicense(id: String, clienteId: String) {
        viewModelScope.launch {
            try {
                repository.deleteLicense(id)
                loadLicencias(clienteId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
