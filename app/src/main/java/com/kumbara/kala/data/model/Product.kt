package com.kumbara.kala.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val category: String,
    val imagePath: String = "",
    val benefitCard: String = "",
    val story: String = "",
    val careGuide: String = "",
    val createdAt: Long = System.currentTimeMillis()
)