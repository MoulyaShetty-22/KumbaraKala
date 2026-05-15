package com.kumbara.kala.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kumbara.kala.data.model.Artisan

@Dao
interface ArtisanDao {
    @Query("SELECT * FROM artisan WHERE id = 1")
    fun getArtisan(): LiveData<Artisan?>

    @Query("SELECT * FROM artisan WHERE id = 1")
    suspend fun getArtisanOnce(): Artisan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtisan(artisan: Artisan)

    @Update
    suspend fun updateArtisan(artisan: Artisan)

    @Query("UPDATE artisan SET lastDailyFact = :fact, lastDailyFactDate = :date WHERE id = 1")
    suspend fun updateDailyFact(fact: String, date: String)
}
