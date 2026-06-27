package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.Product

class ProductRepository {
    suspend fun getProducts(almacenId: String): List<Product> {
        return try {
            SupabaseClientProvider.client
                .from("productos")
                .select { filter { eq("almacen_id", almacenId) } }
                .decodeAs<List<Product>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createProduct(product: Product): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("productos")
                .insert(mapOf(
                    "nombre" to product.nombre,
                    "precio" to product.precio,
                    "stock" to product.stock,
                    "almacen_id" to product.almacen_id
                ))
            true
        } catch (e: Exception) { false }
    }

    suspend fun updateProduct(id: String, product: Product): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("productos")
                .update(mapOf(
                    "nombre" to product.nombre,
                    "precio" to product.precio,
                    "stock" to product.stock
                )) { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteProduct(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("productos")
                .delete { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }
}
