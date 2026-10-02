package com.example.model

enum class ZodiacSign(
    val signName: String,
    val symbol: String,
    val dates: String,
    val rulingPlanet: String,
    val element: String,
    val luckyNumbers: List<Int>,
    val cosmicVibe: String
) {
    ARIES("Aries", "♈", "Mar 21 - Apr 19", "Mars", "Fire", listOf(1, 9, 18, 27, 36, 45), "Courageous & Direct"),
    TAURUS("Taurus", "♉", "Apr 20 - May 20", "Venus", "Earth", listOf(2, 6, 15, 24, 33, 42), "Grounded & Abundant"),
    GEMINI("Gemini", "♊", "May 21 - Jun 20", "Mercury", "Air", listOf(3, 5, 12, 23, 32, 41), "Dual & Quick-Witted"),
    CANCER("Cancer", "♋", "Jun 21 - Jul 22", "Moon", "Water", listOf(2, 7, 11, 20, 29, 38), "Intuitive & Magnetic"),
    LEO("Leo", "♌", "Jul 23 - Aug 22", "Sun", "Fire", listOf(1, 4, 10, 19, 28, 37), "Radiant & Fortunate"),
    VIRGO("Virgo", "♍", "Aug 23 - Sep 22", "Mercury", "Earth", listOf(5, 14, 23, 32, 41, 50), "Methodical & Precise"),
    LIBRA("Libra", "♎", "Sep 23 - Oct 22", "Venus", "Air", listOf(6, 15, 24, 33, 42, 51), "Balanced & Harmonious"),
    SCORPIO("Scorpio", "♏", "Oct 23 - Nov 21", "Pluto/Mars", "Water", listOf(8, 17, 26, 35, 44, 53), "Deep & Transformative"),
    SAGITTARIUS("Sagittarius", "♐", "Nov 22 - Dec 21", "Jupiter", "Fire", listOf(3, 9, 12, 21, 30, 39), "Lucky & Expansive"),
    CAPRICORN("Capricorn", "♑", "Dec 22 - Jan 19", "Saturn", "Earth", listOf(4, 8, 13, 22, 31, 40), "Disciplined & Enduring"),
    AQUARIUS("Aquarius", "♒", "Jan 20 - Feb 18", "Uranus", "Air", listOf(4, 7, 11, 22, 29, 38), "Visionary & Eccentric"),
    PISCES("Pisces", "♓", "Feb 19 - Mar 20", "Neptune", "Water", listOf(3, 7, 12, 16, 25, 34), "Mystic & Dreamer")
}
