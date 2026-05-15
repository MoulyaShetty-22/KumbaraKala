package com.kumbara.kala.data.repository

import androidx.lifecycle.LiveData
import com.kumbara.kala.data.db.ArtisanDao
import com.kumbara.kala.data.model.Artisan

class ArtisanRepository(private val artisanDao: ArtisanDao) {
    val artisan: LiveData<Artisan?> = artisanDao.getArtisan()

    suspend fun getArtisanOnce(): Artisan? = artisanDao.getArtisanOnce()
    suspend fun insertArtisan(artisan: Artisan) = artisanDao.insertArtisan(artisan)
    suspend fun updateArtisan(artisan: Artisan) = artisanDao.updateArtisan(artisan)
    suspend fun updateDailyFact(fact: String, date: String) = artisanDao.updateDailyFact(fact, date)
}
