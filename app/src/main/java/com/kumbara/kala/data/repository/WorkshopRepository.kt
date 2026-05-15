package com.kumbara.kala.data.repository

import androidx.lifecycle.LiveData
import com.kumbara.kala.data.db.WorkshopDao
import com.kumbara.kala.data.model.Workshop

class WorkshopRepository(private val workshopDao: WorkshopDao) {
    val allWorkshops: LiveData<List<Workshop>> = workshopDao.getAllWorkshops()

    suspend fun getWorkshopById(id: Long): Workshop? = workshopDao.getWorkshopById(id)
    suspend fun insertWorkshop(workshop: Workshop): Long = workshopDao.insertWorkshop(workshop)
    suspend fun updateWorkshop(workshop: Workshop) = workshopDao.updateWorkshop(workshop)
    suspend fun deleteWorkshop(workshop: Workshop) = workshopDao.deleteWorkshop(workshop)
    suspend fun updateSlots(id: Long, slots: Int) = workshopDao.updateSlots(id, slots)
}
