package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_predictions")
data class SavedPredictionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gameName: String,
    val strategyName: String,
    val primaryBalls: String, // comma separated numbers e.g. "1,15,23,37,61"
    val specialBall: Int,
    val astralResonance: Int,
    val sumTotal: Int,
    val astrologicalAspect: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "historical_draws")
data class HistoricalDrawEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gameName: String,
    val drawString: String, // e.g. "1,15,23,37,61,18"
    val timestamp: Long = System.currentTimeMillis()
)
