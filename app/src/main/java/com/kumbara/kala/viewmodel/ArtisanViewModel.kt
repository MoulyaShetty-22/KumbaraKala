package com.kumbara.kala.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kumbara.kala.data.db.AppDatabase
import com.kumbara.kala.data.model.Artisan
import com.kumbara.kala.data.repository.ArtisanRepository
import com.kumbara.kala.data.repository.GeminiRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ArtisanViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ArtisanRepository
    private val geminiRepository = GeminiRepository()
    val artisan: LiveData<Artisan?>
    private val _dailyFact = MutableLiveData<String?>()
    val dailyFact: LiveData<String?> = _dailyFact
    private val _isLoadingFact = MutableLiveData<Boolean>()
    val isLoadingFact: LiveData<Boolean> = _isLoadingFact
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ArtisanRepository(db.artisanDao())
        artisan = repository.artisan
    }

    fun saveArtisan(artisan: Artisan) = viewModelScope.launch {
        val existing = repository.getArtisanOnce()
        if (existing == null) repository.insertArtisan(artisan)
        else repository.updateArtisan(artisan)
    }

    fun fetchDailyFact(language: String = "English") {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val current = repository.getArtisanOnce()
            // Use cached fact if already fetched today
            if (current?.lastDailyFactDate == today && !current?.lastDailyFact.isNullOrBlank()) {
                _dailyFact.value = current?.lastDailyFact
                return@launch
            }
            _isLoadingFact.value = true
            val result = geminiRepository.generateDailyFact(language)
            result.onSuccess { fact ->
                repository.updateDailyFact(fact, today)
                _dailyFact.value = fact
            }.onFailure {
                // Show a static fallback fact if API fails
                _dailyFact.value = "Did you know? Clay water pots cool water naturally through evaporation — no electricity needed. This ancient technology keeps water up to 8°C cooler than room temperature!"
            }
            _isLoadingFact.value = false
        }
    }

    fun clearError() { _errorMessage.value = null }
}
