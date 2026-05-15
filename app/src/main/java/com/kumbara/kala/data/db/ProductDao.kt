package com.kumbara.kala.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kumbara.kala.data.model.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET benefitCard = :card WHERE id = :id")
    suspend fun updateBenefitCard(id: Long, card: String)

    @Query("UPDATE products SET story = :story WHERE id = :id")
    suspend fun updateStory(id: Long, story: String)

    @Query("UPDATE products SET careGuide = :guide WHERE id = :id")
    suspend fun updateCareGuide(id: Long, guide: String)
}
