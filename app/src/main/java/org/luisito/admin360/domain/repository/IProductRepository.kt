package org.luisito.admin360.domain.repository

import org.luisito.admin360.domain.result.Result
import org.luisito.admin360.data.models.Product

interface IProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
    suspend fun createProduct(product: Product): Result<Unit>
    suspend fun updateProduct(id: String, product: Product): Result<Unit>
    suspend fun deleteProduct(id: String): Result<Unit>
    suspend fun syncProducts(): Result<Unit>
}
