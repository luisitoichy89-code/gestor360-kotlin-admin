package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Traza
import org.luisito.admin360.data.repository.TrazaRepository

data class TrazaUiState(
    val isLoading: Boolean = false,
    val trazas: List<Traza> = emptyList(),
    val error: String? = null
)

class TrazaViewModel(
    private val repository: TrazaRepository = TrazaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrazaUiState())
    val uiState: StateFlow<TrazaUiState> = _uiState.asStateFlow()

    fun loadTrazas(almacenId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val trazas = repository.getTrazas(almacenId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    trazas = trazas,
                    error = if (trazas.isEmpty()) "No hay trazas" else null
                )
            }
        }
    }
}
