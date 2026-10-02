package com.example.model

data class ParsedDraw(
    val mainNumbers: List<Int>,
    val specialBall: Int,
    val rawText: String,
    val isValid: Boolean,
    val errorMessage: String? = null
)

data class AstroPrediction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val game: LotteryGame,
    val primaryBalls: List<Int>, // Sorted main numbers
    val specialBall: Int,
    val strategyName: String,
    val astralResonance: Int, // e.g. 96 (%)
    val sumTotal: Int,
    val oddEvenRatio: String, // e.g. "3 Odd / 2 Even"
    val highLowRatio: String, // e.g. "3 High / 2 Low"
    val astrologicalAspect: String,
    val celestialQuote: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DrawingStats(
    val totalDraws: Int,
    val hotMainBalls: List<Pair<Int, Int>>, // number to frequency
    val coldMainBalls: List<Pair<Int, Int>>,
    val hotSpecialBalls: List<Pair<Int, Int>>,
    val coldSpecialBalls: List<Pair<Int, Int>>,
    val allMainFrequencies: Map<Int, Int>,
    val allSpecialFrequencies: Map<Int, Int>,
    val averageSum: Double,
    val mostCommonOddCount: Int
)
