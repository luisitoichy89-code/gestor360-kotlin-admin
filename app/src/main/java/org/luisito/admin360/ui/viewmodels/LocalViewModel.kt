package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.repository.LocalRepository

data class LocalUiState(
    val isLoading: Boolean = false,
    val locales: List<Local> = emptyList(),
    val error: String? = null
)

class LocalViewModel(
    private val repository: LocalRepository = LocalRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocalUiState())
    val uiState: StateFlow<LocalUiState> = _uiState.asStateFlow()

    fun loadLocales(clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.getLocales(clienteId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    locales = it,
                    error = null
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }

    fun createLocal(clienteId: String, nombre: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.createLocal(clienteId, nombre)

            result.onSuccess {
                loadLocales(clienteId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }

    fun updateLocal(id: String, nombre: String, activo: Boolean, clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.updateLocal(id, nombre, activo)

            result.onSuccess {
                loadLocales(clienteId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }

    fun deleteLocal(id: String, clienteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = repository.deleteLocal(id)

            result.onSuccess {
                loadLocales(clienteId)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }
}
