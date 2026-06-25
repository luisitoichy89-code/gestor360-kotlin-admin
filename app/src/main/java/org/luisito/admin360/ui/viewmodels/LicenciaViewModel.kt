package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Licencia
import org.luisito.admin360.data.repository.LicenciaRepository

data class LicenciaUiState(
    val licencias: List<Licencia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LicenciaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LicenciaUiState())
    val uiState: StateFlow<LicenciaUiState> = _uiState
    private val repo = LicenciaRepository()

    fun loadLicencias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val lics = repo.getAllLicencias()
                _uiState.value = _uiState.value.copy(licencias = lics, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun activateLicencia(deviceId: String, almacenId: String, dias: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val ok = repo.activateLicencia(deviceId, almacenId, dias)
            if (ok) loadLicencias() else _uiState.value = _uiState.value.copy(error = "Error al activar", isLoading = false)
        }
    }

    fun renewLicencia(id: Int, dias: Int) {
        viewModelScope.launch {
            val ok = repo.renewLicencia(id, dias)
            if (ok) loadLicencias()
        }
    }

    fun deleteLicencia(id: Int) {
        viewModelScope.launch {
            val ok = repo.deleteLicencia(id)
            if (ok) loadLicencias()
        }
    }
}
