package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AstroDao {

    // Saved Predictions
    @Query("SELECT * FROM saved_predictions ORDER BY timestamp DESC")
    fun getAllSavedPredictions(): Flow<List<SavedPredictionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: SavedPredictionEntity): Long

    @Query("DELETE FROM saved_predictions WHERE id = :id")
    suspend fun deletePredictionById(id: Int)

    @Query("DELETE FROM saved_predictions")
    suspend fun clearAllPredictions()

    // Historical Drawings
    @Query("SELECT * FROM historical_draws WHERE gameName = :gameName ORDER BY id DESC")
    fun getDrawsForGame(gameName: String): Flow<List<HistoricalDrawEntity>>

    @Query("SELECT * FROM historical_draws ORDER BY id DESC")
    fun getAllDraws(): Flow<List<HistoricalDrawEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraw(draw: HistoricalDrawEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraws(draws: List<HistoricalDrawEntity>)

    @Query("DELETE FROM historical_draws WHERE gameName = :gameName")
    suspend fun clearDrawsForGame(gameName: String)
}
