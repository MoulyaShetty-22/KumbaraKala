package com.kumbara.kala.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artisan")
data class Artisan(
    @PrimaryKey
    val id: Int = 1, // Single artisan per device
    val name: String = "Your Name",
    val village: String = "",
    val yearsOfCraft: Int = 0,
    val biography: String = "",
    val phone: String = "",
    val heritageTags: String = "Kumbara,Karnataka,Clay",
    val profileImagePath: String = "",
    val lastDailyFact: String = "",
    val lastDailyFactDate: String = ""
)
