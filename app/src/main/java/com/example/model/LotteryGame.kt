package com.example.model

enum class LotteryGame(
    val displayName: String,
    val subtitle: String,
    val mainBallCount: Int,
    val mainBallMax: Int,
    val specialBallName: String,
    val specialBallMax: Int,
    val sampleDraws: List<String>
) {
    PA_POWERBALL(
        displayName = "PA Powerball",
        subtitle = "5 balls (1-69) + Powerball (1-26)",
        mainBallCount = 5,
        mainBallMax = 69,
        specialBallName = "Powerball",
        specialBallMax = 26,
        sampleDraws = listOf(
            "1,15,23,37,61,18",
            "7,11,19,53,68,23",
            "12,25,30,44,69,9",
            "3,18,27,36,55,14",
            "2,21,38,61,66,12",
            "10,19,26,28,50,16",
            "5,14,31,40,54,20"
        )
    )
}
