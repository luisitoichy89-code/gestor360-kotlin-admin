package org.luisito.admin360.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.luisito.admin360.data.models.Product
import org.luisito.admin360.data.repository.ProductRepository

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

class ProductsViewModel(
    private val repository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    fun loadProducts(almacenId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val products = repository.getProducts(almacenId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    products = products,
                    error = if (products.isEmpty()) "No hay productos" else null
                )
            }
        }
    }

    fun createProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.createProduct(product)
            if (success) {
                loadProducts(product.almacen_id)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al crear producto") }
            }
        }
    }

    fun updateProduct(id: String, product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.updateProduct(id, product)
            if (success) {
                loadProducts(product.almacen_id)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al actualizar") }
            }
        }
    }

    fun deleteProduct(id: String, almacenId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.deleteProduct(id)
            if (success) {
                loadProducts(almacenId)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al eliminar") }
            }
        }
    }
}
