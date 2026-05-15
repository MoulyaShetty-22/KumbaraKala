package com.kumbara.kala.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kumbara.kala.data.model.Workshop

@Dao
interface WorkshopDao {
    @Query("SELECT * FROM workshops ORDER BY date ASC")
    fun getAllWorkshops(): LiveData<List<Workshop>>

    @Query("SELECT * FROM workshops WHERE id = :id")
    suspend fun getWorkshopById(id: Long): Workshop?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkshop(workshop: Workshop): Long

    @Update
    suspend fun updateWorkshop(workshop: Workshop)

    @Delete
    suspend fun deleteWorkshop(workshop: Workshop)

    @Query("UPDATE workshops SET slots = :slots WHERE id = :id")
    suspend fun updateSlots(id: Long, slots: Int)
}
