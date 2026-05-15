package com.kumbara.kala.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workshops")
data class Workshop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String,       // yyyy-MM-dd
    val duration: String,   // e.g. "2 hours"
    val price: Double,
    val slots: Int,
    val location: String,
    val createdAt: Long = System.currentTimeMillis()
)
