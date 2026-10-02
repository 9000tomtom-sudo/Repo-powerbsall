package com.example.engine

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class AstroCelestialState(
    val timestamp: Long,
    val formattedDateTime: String,
    val formattedTime: String,
    val formattedDate: String,
    val currentSunSign: String,
    val currentSunSignSymbol: String,
    val lunarPhaseName: String,
    val lunarPhaseSymbol: String,
    val lunarIlluminationPct: Int,
    val planetaryDayRuler: String,
    val planetaryHourRuler: String,
    val nextPowerballDate: String,
    val nextPowerballCountdown: String,
    val cosmicVibrationAspect: String,
    val astroHarmonicMultiplier: Double
)

object AstroClockEngine {

    // Chaldean planetary order: Saturn, Jupiter, Mars, Sun, Venus, Mercury, Moon
    private val chaldeanOrder = listOf("Saturn", "Jupiter", "Mars", "Sun", "Venus", "Mercury", "Moon")

    // Day rulers (Sunday to Saturday)
    private val dayRulers = mapOf(
        Calendar.SUNDAY to "Sun",
        Calendar.MONDAY to "Moon",
        Calendar.TUESDAY to "Mars",
        Calendar.WEDNESDAY to "Mercury",
        Calendar.THURSDAY to "Jupiter",
        Calendar.FRIDAY to "Venus",
        Calendar.SATURDAY to "Saturn"
    )

    fun calculateCelestialState(currentTimeMillis: Long = System.currentTimeMillis()): AstroCelestialState {
        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = currentTimeMillis
        }

        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm:ss z", Locale.US)
        val fullFormat = SimpleDateFormat("EEEE, MMMM d, yyyy • hh:mm:ss a", Locale.US)

        val formattedDate = dateFormat.format(Date(currentTimeMillis))
        val formattedTime = timeFormat.format(Date(currentTimeMillis))
        val formattedDateTime = fullFormat.format(Date(currentTimeMillis))

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        // 1. Planetary Day Ruler
        val dayRuler = dayRulers[dayOfWeek] ?: "Sun"

        // 2. Planetary Hour Ruler (Chaldean calculation)
        val dayRulerStartIndex = chaldeanOrder.indexOf(dayRuler).coerceAtLeast(0)
        val hourRulerIndex = (dayRulerStartIndex + hourOfDay) % chaldeanOrder.size
        val hourRuler = chaldeanOrder[hourRulerIndex]

        // 3. Sun Sign by Day of Year
        val (sunSign, sunSymbol) = determineSunSign(calendar)

        // 4. Lunar Phase & Illumination Calculation
        val (moonName, moonSymbol, illumination) = calculateMoonPhase(currentTimeMillis)

        // 5. Next PA Powerball Drawing Countdown
        // Powerball draws on Monday, Wednesday, Saturday at 10:59 PM ET (22:59)
        val (nextDrawStr, countdownStr) = calculateNextPowerballDraw(currentTimeMillis)

        val cosmicVibe = "In ${hourRuler}'s celestial hour under ${moonName} ($illumination% illumination). " +
                "Sun in $sunSign governs vibrational field."

        val multiplier = 1.0 + (illumination / 100.0) * 0.15

        return AstroCelestialState(
            timestamp = currentTimeMillis,
            formattedDateTime = formattedDateTime,
            formattedTime = formattedTime,
            formattedDate = formattedDate,
            currentSunSign = sunSign,
            currentSunSignSymbol = sunSymbol,
            lunarPhaseName = moonName,
            lunarPhaseSymbol = moonSymbol,
            lunarIlluminationPct = illumination,
            planetaryDayRuler = dayRuler,
            planetaryHourRuler = hourRuler,
            nextPowerballDate = nextDrawStr,
            nextPowerballCountdown = countdownStr,
            cosmicVibrationAspect = cosmicVibe,
            astroHarmonicMultiplier = multiplier
        )
    }

    private fun determineSunSign(cal: Calendar): Pair<String, String> {
        val month = cal.get(Calendar.MONTH) + 1 // 1-12
        val day = cal.get(Calendar.DAY_OF_MONTH)

        return when {
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Aries" to "♈"
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Taurus" to "♉"
            (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Gemini" to "♊"
            (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Cancer" to "♋"
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Leo" to "♌"
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Virgo" to "♍"
            (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Libra" to "♎"
            (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Scorpio" to "♏"
            (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Sagittarius" to "♐"
            (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "Capricorn" to "♑"
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Aquarius" to "♒"
            else -> "Pisces" to "♓"
        }
    }

    /**
     * Calculates moon phase using synodic lunar cycle (~29.53058867 days)
     * Reference new moon: Jan 11, 2024, 11:57 UTC
     */
    private fun calculateMoonPhase(timeMillis: Long): Triple<String, String, Int> {
        val refNewMoonMillis = 1704974220000L // Jan 11, 2024
        val synodicMonthMillis = 29.53058867 * 24 * 60 * 60 * 1000.0

        val diff = (timeMillis - refNewMoonMillis).toDouble()
        val cycles = diff / synodicMonthMillis
        val phaseFraction = cycles - kotlin.math.floor(cycles) // 0.0 to 1.0

        // Illumination curve (approx 0 to 100%)
        val illumination = ((1.0 - kotlin.math.cos(phaseFraction * 2 * Math.PI)) / 2.0 * 100).toInt()

        val (name, symbol) = when {
            phaseFraction < 0.03 || phaseFraction >= 0.97 -> "New Moon" to "🌑"
            phaseFraction < 0.22 -> "Waxing Crescent" to "🌒"
            phaseFraction < 0.28 -> "First Quarter" to "🌓"
            phaseFraction < 0.47 -> "Waxing Gibbous" to "🌔"
            phaseFraction < 0.53 -> "Full Moon" to "🌕"
            phaseFraction < 0.72 -> "Waning Gibbous" to "🌖"
            phaseFraction < 0.78 -> "Third Quarter" to "🌗"
            else -> "Waning Crescent" to "🌘"
        }

        return Triple(name, symbol, illumination)
    }

    /**
     * Computes the next Pennsylvania Powerball drawing:
     * Mondays, Wednesdays, Saturdays at 22:59 ET
     */
    private fun calculateNextPowerballDraw(nowMillis: Long): Pair<String, String> {
        val etTz = TimeZone.getTimeZone("America/New_York")
        val cal = Calendar.getInstance(etTz).apply { timeInMillis = nowMillis }

        // Find next draw time
        val drawDays = setOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.SATURDAY)

        var candidate = Calendar.getInstance(etTz).apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (candidate.timeInMillis <= nowMillis || candidate.get(Calendar.DAY_OF_WEEK) !in drawDays) {
            do {
                candidate.add(Calendar.DAY_OF_YEAR, 1)
            } while (candidate.get(Calendar.DAY_OF_WEEK) !in drawDays)
        }

        val diffMs = candidate.timeInMillis - nowMillis
        val diffSec = (diffMs / 1000).coerceAtLeast(0)
        val days = diffSec / (24 * 3600)
        val hours = (diffSec % (24 * 3600)) / 3600
        val mins = (diffSec % 3600) / 60
        val secs = diffSec % 60

        val countdown = if (days > 0) {
            "${days}d ${hours}h ${mins}m ${secs}s"
        } else {
            "${hours}h ${mins}m ${secs}s"
        }

        val drawFmt = SimpleDateFormat("EEE, MMM d 'at 10:59 PM ET'", Locale.US).apply {
            timeZone = etTz
        }
        val drawStr = drawFmt.format(candidate.time)

        return Pair(drawStr, countdown)
    }
}
