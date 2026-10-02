package com.example.data.local

import kotlinx.coroutines.flow.Flow

class AstroRepository(private val dao: AstroDao) {

    val savedPredictions: Flow<List<SavedPredictionEntity>> = dao.getAllSavedPredictions()

    suspend fun savePrediction(prediction: SavedPredictionEntity): Long {
        return dao.insertPrediction(prediction)
    }

    suspend fun deletePrediction(id: Int) {
        dao.deletePredictionById(id)
    }

    suspend fun clearPredictions() {
        dao.clearAllPredictions()
    }

    fun getDrawsForGame(gameName: String): Flow<List<HistoricalDrawEntity>> {
        return dao.getDrawsForGame(gameName)
    }

    suspend fun saveDraws(gameName: String, draws: List<String>) {
        val entities = draws.map { HistoricalDrawEntity(gameName = gameName, drawString = it) }
        dao.insertDraws(entities)
    }

    suspend fun clearDrawsForGame(gameName: String) {
        dao.clearDrawsForGame(gameName)
    }
}
