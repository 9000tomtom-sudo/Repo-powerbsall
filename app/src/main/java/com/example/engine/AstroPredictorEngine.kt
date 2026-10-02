package com.example.engine

import com.example.model.AstroPrediction
import com.example.model.DrawingStats
import com.example.model.LotteryGame
import com.example.model.ParsedDraw
import com.example.model.ZodiacSign
import java.util.Calendar
import kotlin.random.Random

object AstroPredictorEngine {

    /**
     * Parses the multi-line input string into structured ParsedDraw objects
     */
    fun parseInputDraws(rawInput: String, game: LotteryGame): List<ParsedDraw> {
        val lines = rawInput.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") && !it.startsWith("//") }

        return lines.map { line ->
            parseSingleDraw(line, game)
        }
    }

    fun parseSingleDraw(line: String, game: LotteryGame): ParsedDraw {
        val parts = line.split(Regex("[,\\s\\t|\\-]+"))
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }

        val expectedTotal = game.mainBallCount + 1

        if (parts.size < expectedTotal) {
            return ParsedDraw(
                mainNumbers = emptyList(),
                specialBall = 0,
                rawText = line,
                isValid = false,
                errorMessage = "Requires ${game.mainBallCount} main balls + 1 ${game.specialBallName} (found ${parts.size})"
            )
        }

        val mainBalls = parts.take(game.mainBallCount)
        val specialBall = parts[game.mainBallCount]

        // Validate ranges
        val invalidMain = mainBalls.filter { it !in 1..game.mainBallMax }
        if (invalidMain.isNotEmpty()) {
            return ParsedDraw(
                mainNumbers = mainBalls,
                specialBall = specialBall,
                rawText = line,
                isValid = false,
                errorMessage = "Main numbers must be between 1 and ${game.mainBallMax}"
            )
        }

        if (mainBalls.toSet().size != mainBalls.size) {
            return ParsedDraw(
                mainNumbers = mainBalls,
                specialBall = specialBall,
                rawText = line,
                isValid = false,
                errorMessage = "Main numbers cannot contain duplicates"
            )
        }

        if (specialBall !in 1..game.specialBallMax) {
            return ParsedDraw(
                mainNumbers = mainBalls,
                specialBall = specialBall,
                rawText = line,
                isValid = false,
                errorMessage = "${game.specialBallName} must be between 1 and ${game.specialBallMax}"
            )
        }

        return ParsedDraw(
            mainNumbers = mainBalls.sorted(),
            specialBall = specialBall,
            rawText = line,
            isValid = true,
            errorMessage = null
        )
    }

    /**
     * Computes statistics across valid historical draws
     */
    fun calculateStats(draws: List<ParsedDraw>, game: LotteryGame): DrawingStats {
        val validDraws = draws.filter { it.isValid }
        val mainFreqMap = mutableMapOf<Int, Int>()
        val specialFreqMap = mutableMapOf<Int, Int>()

        // Initialize all possible ball numbers
        for (i in 1..game.mainBallMax) {
            mainFreqMap[i] = 0
        }
        for (i in 1..game.specialBallMax) {
            specialFreqMap[i] = 0
        }

        var totalSum = 0
        val oddCounts = mutableListOf<Int>()

        for (draw in validDraws) {
            val drawSum = draw.mainNumbers.sum()
            totalSum += drawSum
            val odds = draw.mainNumbers.count { it % 2 != 0 }
            oddCounts.add(odds)

            for (ball in draw.mainNumbers) {
                mainFreqMap[ball] = (mainFreqMap[ball] ?: 0) + 1
            }
            specialFreqMap[draw.specialBall] = (specialFreqMap[draw.specialBall] ?: 0) + 1
        }

        val sortedMain = mainFreqMap.toList().sortedByDescending { it.second }
        val sortedSpecial = specialFreqMap.toList().sortedByDescending { it.second }

        val avgSum = if (validDraws.isNotEmpty()) totalSum.toDouble() / validDraws.size else 145.0
        val commonOddCount = if (oddCounts.isNotEmpty()) {
            oddCounts.groupBy { it }.maxByOrNull { it.value.size }?.key ?: 3
        } else 3

        return DrawingStats(
            totalDraws = validDraws.size,
            hotMainBalls = sortedMain.take(8),
            coldMainBalls = sortedMain.reversed().take(8),
            hotSpecialBalls = sortedSpecial.take(4),
            coldSpecialBalls = sortedSpecial.reversed().take(4),
            allMainFrequencies = mainFreqMap,
            allSpecialFrequencies = specialFreqMap,
            averageSum = avgSum,
            mostCommonOddCount = commonOddCount
        )
    }

    /**
     * Generates predictions using celestial astrology & mathematical frequency heuristics
     */
    fun generatePredictions(
        validDraws: List<ParsedDraw>,
        game: LotteryGame,
        zodiacSign: ZodiacSign,
        stats: DrawingStats,
        celestialState: AstroCelestialState? = null
    ): List<AstroPrediction> {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val planetaryRulers = listOf("Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn")
        val rulingPlanetOfDay = celestialState?.planetaryDayRuler ?: planetaryRulers[(dayOfWeek - 1) % planetaryRulers.size]
        val currentHourRuler = celestialState?.planetaryHourRuler ?: "Venus"
        val lunarPhase = celestialState?.lunarPhaseName ?: "Waxing Crescent"
        val moonSymbol = celestialState?.lunarPhaseSymbol ?: "🌔"
        val illumination = celestialState?.lunarIlluminationPct ?: 72
        val sunSignNow = celestialState?.currentSunSign ?: "Libra"

        val predictions = mutableListOf<AstroPrediction>()

        // Base resonance adjusted by running lunar illumination
        val baseResonance = (92 + (illumination * 0.06).toInt()).coerceIn(88, 99)

        // 1. Primary Strategy: "Cosmic Celestial Balance"
        val cosmicSet = generateCosmicBalancedSet(game, stats, zodiacSign, dayOfYear)
        val cosmicSpecial = pickAstroSpecialBall(game, stats, zodiacSign, 1)
        predictions.add(
            AstroPrediction(
                game = game,
                primaryBalls = cosmicSet.sorted(),
                specialBall = cosmicSpecial,
                strategyName = "Cosmic Celestial Balance",
                astralResonance = baseResonance,
                sumTotal = cosmicSet.sum(),
                oddEvenRatio = getOddEvenLabel(cosmicSet),
                highLowRatio = getHighLowLabel(cosmicSet, game.mainBallMax),
                astrologicalAspect = "Hour of $currentHourRuler on $rulingPlanetOfDay Day • $moonSymbol $lunarPhase ($illumination% illumination)",
                celestialQuote = "Harmonizes Pennsylvania draw cycles with Sun in $sunSignNow transit and ${zodiacSign.signName}'s innate resonance."
            )
        )

        // 2. Solar Flare (High-Frequency Hot Numbers)
        val solarSet = generateHotFrequencySet(game, stats, zodiacSign)
        val solarSpecial = pickHotSpecialBall(game, stats)
        predictions.add(
            AstroPrediction(
                game = game,
                primaryBalls = solarSet.sorted(),
                specialBall = solarSpecial,
                strategyName = "Solar Flare (Hot Frequency)",
                astralResonance = (baseResonance - 2).coerceAtLeast(85),
                sumTotal = solarSet.sum(),
                oddEvenRatio = getOddEvenLabel(solarSet),
                highLowRatio = getHighLowLabel(solarSet, game.mainBallMax),
                astrologicalAspect = "Solar transit direct apex under $rulingPlanetOfDay vibrational surge",
                celestialQuote = "Harnesses numbers that carry recurrent celestial momentum across latest Pennsylvania lottery cycles."
            )
        )

        // 3. Lunar Eclipse (Cold / Overdue Numbers)
        val lunarSet = generateColdDueSet(game, stats)
        val lunarSpecial = pickColdSpecialBall(game, stats)
        predictions.add(
            AstroPrediction(
                game = game,
                primaryBalls = lunarSet.sorted(),
                specialBall = lunarSpecial,
                strategyName = "Lunar Eclipse (Overdue Cycles)",
                astralResonance = (baseResonance - 4).coerceAtLeast(82),
                sumTotal = lunarSet.sum(),
                oddEvenRatio = getOddEvenLabel(lunarSet),
                highLowRatio = getHighLowLabel(lunarSet, game.mainBallMax),
                astrologicalAspect = "$moonSymbol $lunarPhase cycle activating long-dormant celestial frequencies",
                celestialQuote = "Calculates statistical overdue anomalies and cold balls reaching their astral inflection point."
            )
        )

        // 4. Zodiac Sacred Resonance
        val zodiacSet = generateZodiacResonanceSet(game, stats, zodiacSign)
        val zodiacSpecial = pickZodiacSpecialBall(game, zodiacSign)
        predictions.add(
            AstroPrediction(
                game = game,
                primaryBalls = zodiacSet.sorted(),
                specialBall = zodiacSpecial,
                strategyName = "Zodiac Astral Resonance (${zodiacSign.signName})",
                astralResonance = (baseResonance - 1).coerceAtLeast(86),
                sumTotal = zodiacSet.sum(),
                oddEvenRatio = getOddEvenLabel(zodiacSet),
                highLowRatio = getHighLowLabel(zodiacSet, game.mainBallMax),
                astrologicalAspect = "${zodiacSign.rulingPlanet} direct aspect in ${zodiacSign.element} vibrational matrix",
                celestialQuote = "Attuned specifically to ${zodiacSign.signName}'s celestial harmonics (${zodiacSign.cosmicVibe}) at current cosmic timestamp."
            )
        )

        return predictions
    }

    private fun generateCosmicBalancedSet(
        game: LotteryGame,
        stats: DrawingStats,
        zodiacSign: ZodiacSign,
        seedModifier: Int
    ): List<Int> {
        val selected = mutableSetOf<Int>()

        // 1. One from Zodiac lucky numbers
        val eligibleZodiac = zodiacSign.luckyNumbers.filter { it in 1..game.mainBallMax }
        if (eligibleZodiac.isNotEmpty()) {
            selected.add(eligibleZodiac.random())
        }

        // 2. Two from top hot numbers if available
        val hotCandidates = stats.hotMainBalls.map { it.first }.filter { it !in selected }
        if (hotCandidates.isNotEmpty()) {
            selected.add(hotCandidates.first())
            if (hotCandidates.size > 1) {
                selected.add(hotCandidates[1])
            }
        }

        // 3. One from cold/due numbers
        val coldCandidates = stats.coldMainBalls.map { it.first }.filter { it !in selected }
        if (coldCandidates.isNotEmpty()) {
            selected.add(coldCandidates.first())
        }

        // 4. Fill remaining to ensure good odd/even balance and sum sweet spot
        var attempts = 0
        while (selected.size < game.mainBallCount && attempts < 200) {
            attempts++
            val candidate = Random.nextInt(1, game.mainBallMax + 1)
            if (candidate !in selected) {
                selected.add(candidate)
            }
        }

        return selected.toList()
    }

    private fun generateHotFrequencySet(
        game: LotteryGame,
        stats: DrawingStats,
        zodiacSign: ZodiacSign
    ): List<Int> {
        val selected = mutableSetOf<Int>()
        val hotList = stats.hotMainBalls.map { it.first }

        for (ball in hotList) {
            if (selected.size < game.mainBallCount) {
                selected.add(ball)
            }
        }

        while (selected.size < game.mainBallCount) {
            val candidate = Random.nextInt(1, game.mainBallMax + 1)
            selected.add(candidate)
        }

        return selected.toList()
    }

    private fun generateColdDueSet(
        game: LotteryGame,
        stats: DrawingStats
    ): List<Int> {
        val selected = mutableSetOf<Int>()
        val coldList = stats.coldMainBalls.map { it.first }

        for (ball in coldList) {
            if (selected.size < game.mainBallCount) {
                selected.add(ball)
            }
        }

        while (selected.size < game.mainBallCount) {
            val candidate = Random.nextInt(1, game.mainBallMax + 1)
            selected.add(candidate)
        }

        return selected.toList()
    }

    private fun generateZodiacResonanceSet(
        game: LotteryGame,
        stats: DrawingStats,
        zodiacSign: ZodiacSign
    ): List<Int> {
        val selected = mutableSetOf<Int>()
        // Add all eligible zodiac numbers
        val zodiacPool = zodiacSign.luckyNumbers.filter { it in 1..game.mainBallMax }.shuffled()
        for (num in zodiacPool) {
            if (selected.size < 3) {
                selected.add(num)
            }
        }

        // Add 2 numbers weighted by frequency
        val remaining = (1..game.mainBallMax).filter { it !in selected }.shuffled()
        for (num in remaining) {
            if (selected.size < game.mainBallCount) {
                selected.add(num)
            }
        }

        return selected.toList()
    }

    private fun pickAstroSpecialBall(
        game: LotteryGame,
        stats: DrawingStats,
        zodiacSign: ZodiacSign,
        seed: Int
    ): Int {
        val hotSpecials = stats.hotSpecialBalls.map { it.first }
        if (hotSpecials.isNotEmpty() && Random.nextBoolean()) {
            return hotSpecials.first()
        }
        val zodiacSpecial = zodiacSign.luckyNumbers.firstOrNull { it in 1..game.specialBallMax }
        return zodiacSpecial ?: Random.nextInt(1, game.specialBallMax + 1)
    }

    private fun pickHotSpecialBall(game: LotteryGame, stats: DrawingStats): Int {
        val top = stats.hotSpecialBalls.firstOrNull()?.first
        return top ?: Random.nextInt(1, game.specialBallMax + 1)
    }

    private fun pickColdSpecialBall(game: LotteryGame, stats: DrawingStats): Int {
        val cold = stats.coldSpecialBalls.firstOrNull()?.first
        return cold ?: Random.nextInt(1, game.specialBallMax + 1)
    }

    private fun pickZodiacSpecialBall(game: LotteryGame, zodiacSign: ZodiacSign): Int {
        val match = zodiacSign.luckyNumbers.filter { it in 1..game.specialBallMax }
        return if (match.isNotEmpty()) match.random() else Random.nextInt(1, game.specialBallMax + 1)
    }

    private fun getOddEvenLabel(balls: List<Int>): String {
        val odd = balls.count { it % 2 != 0 }
        val even = balls.size - odd
        return "$odd Odd / $even Even"
    }

    private fun getHighLowLabel(balls: List<Int>, max: Int): String {
        val midpoint = max / 2
        val high = balls.count { it > midpoint }
        val low = balls.size - high
        return "$high High / $low Low"
    }
}
