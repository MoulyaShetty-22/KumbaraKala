package com.kumbara.kala.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kumbara.kala.data.db.AppDatabase
import com.kumbara.kala.data.model.Workshop
import com.kumbara.kala.data.repository.WorkshopRepository
import kotlinx.coroutines.launch

class WorkshopViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WorkshopRepository
    val allWorkshops: LiveData<List<Workshop>>

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WorkshopRepository(db.workshopDao())
        allWorkshops = repository.allWorkshops
    }

    fun insertWorkshop(workshop: Workshop, onComplete: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repository.insertWorkshop(workshop)
        onComplete(id)
    }

    fun updateWorkshop(workshop: Workshop) = viewModelScope.launch {
        repository.updateWorkshop(workshop)
    }

    fun deleteWorkshop(workshop: Workshop) = viewModelScope.launch {
        repository.deleteWorkshop(workshop)
    }

    fun updateSlots(id: Long, slots: Int) = viewModelScope.launch {
        repository.updateSlots(id, slots)
    }

    fun clearError() { _errorMessage.value = null }
}
