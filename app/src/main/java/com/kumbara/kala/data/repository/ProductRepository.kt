package com.kumbara.kala.data.repository

import androidx.lifecycle.LiveData
import com.kumbara.kala.data.db.ProductDao
import com.kumbara.kala.data.model.Product

class ProductRepository(private val productDao: ProductDao) {
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)
    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    suspend fun updateBenefitCard(id: Long, card: String) = productDao.updateBenefitCard(id, card)
    suspend fun updateStory(id: Long, story: String) = productDao.updateStory(id, story)
    suspend fun updateCareGuide(id: Long, guide: String) = productDao.updateCareGuide(id, guide)
}
