package com.kumbara.kala.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.kumbara.kala.data.db.AppDatabase
import com.kumbara.kala.data.model.Product
import com.kumbara.kala.data.repository.GeminiRepository
import com.kumbara.kala.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    private val geminiRepository = GeminiRepository()

    val allProducts: LiveData<List<Product>>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _benefitCardResult = MutableLiveData<String?>()
    val benefitCardResult: LiveData<String?> = _benefitCardResult

    private val _storyResult = MutableLiveData<String?>()
    val storyResult: LiveData<String?> = _storyResult

    private val _careGuideResult = MutableLiveData<String?>()
    val careGuideResult: LiveData<String?> = _careGuideResult

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProductRepository(db.productDao())
        allProducts = repository.allProducts
    }

    fun insertProduct(product: Product, onComplete: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repository.insertProduct(product)
        onComplete(id)
    }

    fun updateProduct(product: Product) = viewModelScope.launch {
        repository.updateProduct(product)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch {
        repository.deleteProduct(product)
    }

    fun generateBenefitCard(productId: Long, productName: String, bitmap: Bitmap, language: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = geminiRepository.generateBenefitCard(productName, bitmap, language)
            result.onSuccess { text ->
                repository.updateBenefitCard(productId, text)
                _benefitCardResult.value = text
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to generate benefit card"
            }
            _isLoading.value = false
        }
    }

    fun generateStory(productId: Long, productName: String, language: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = geminiRepository.generateStory(productName, language)
            result.onSuccess { text ->
                repository.updateStory(productId, text)
                _storyResult.value = text
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to generate story"
            }
            _isLoading.value = false
        }
    }

    fun generateCareGuide(productId: Long, productName: String, language: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = geminiRepository.generateCareGuide(productName, language)
            result.onSuccess { text ->
                repository.updateCareGuide(productId, text)
                _careGuideResult.value = text
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Failed to generate care guide"
            }
            _isLoading.value = false
        }
    }

    suspend fun getProductById(id: Long): Product? = repository.getProductById(id)

    fun clearError() { _errorMessage.value = null }
    fun clearBenefitCardResult() { _benefitCardResult.value = null }
    fun clearStoryResult() { _storyResult.value = null }
    fun clearCareGuideResult() { _careGuideResult.value = null }
}
