package com.example.engine

object VoiceInputParser {

    private val wordToNumber = mapOf(
        "zero" to 0, "oh" to 0,
        "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
        "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9, "ten" to 10,
        "eleven" to 11, "twelve" to 12, "thirteen" to 13, "fourteen" to 14,
        "fifteen" to 15, "sixteen" to 16, "seventeen" to 17, "eighteen" to 18,
        "nineteen" to 19, "twenty" to 20, "twenty-one" to 21, "twenty-two" to 22,
        "twenty-three" to 23, "twenty-four" to 24, "twenty-five" to 25,
        "twenty-six" to 26, "twenty-seven" to 27, "twenty-eight" to 28, "twenty-nine" to 29,
        "thirty" to 30, "thirty-one" to 31, "thirty-two" to 32, "thirty-three" to 33,
        "thirty-four" to 34, "thirty-five" to 35, "thirty-six" to 36, "thirty-seven" to 37,
        "thirty-eight" to 38, "thirty-nine" to 39, "forty" to 40, "forty-one" to 41,
        "forty-two" to 42, "forty-three" to 43, "forty-four" to 44, "forty-five" to 45,
        "forty-six" to 46, "forty-seven" to 47, "forty-eight" to 48, "forty-nine" to 49,
        "fifty" to 50, "fifty-one" to 51, "fifty-two" to 52, "fifty-three" to 53,
        "fifty-four" to 54, "fifty-five" to 55, "fifty-six" to 56, "fifty-seven" to 57,
        "fifty-eight" to 58, "fifty-nine" to 59, "sixty" to 60, "sixty-one" to 61,
        "sixty-two" to 62, "sixty-three" to 63, "sixty-four" to 64, "sixty-five" to 65,
        "sixty-six" to 66, "sixty-seven" to 67, "sixty-eight" to 68, "sixty-nine" to 69,
        "seventy" to 70
    )

    /**
     * Extracts numbers from a spoken phrase and formats them into a drawing string:
     * e.g. "one fifteen twenty three thirty seven sixty one powerball eighteen" -> "1,15,23,37,61,18"
     */
    fun parseSpokenTextToDraw(text: String, expectedBallCount: Int = 6): List<String> {
        val cleaned = text.lowercase()
            .replace("power ball", "powerball")
            .replace("mega ball", "megaball")
            .replace("and", " ")
            .replace("ball", " ")
            .replace("draw", " ")
            .replace("next", " ")

        val tokens = cleaned.split(Regex("[\\s,;\\-]+")).filter { it.isNotBlank() }
        val numbers = mutableListOf<Int>()

        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]

            // Check if composite like "twenty" followed by "three"
            if (i + 1 < tokens.size) {
                val combined = "$token-${tokens[i + 1]}"
                if (wordToNumber.containsKey(combined)) {
                    numbers.add(wordToNumber[combined]!!)
                    i += 2
                    continue
                }
                val tens = when (token) {
                    "twenty" -> 20
                    "thirty" -> 30
                    "forty" -> 40
                    "fifty" -> 50
                    "sixty" -> 60
                    "seventy" -> 70
                    else -> 0
                }
                val ones = wordToNumber[tokens[i + 1]] ?: -1
                if (tens > 0 && ones in 1..9) {
                    numbers.add(tens + ones)
                    i += 2
                    continue
                }
            }

            // Check single word number
            if (wordToNumber.containsKey(token)) {
                numbers.add(wordToNumber[token]!!)
            } else {
                // Try parse integer
                val parsed = token.toIntOrNull()
                if (parsed != null) {
                    numbers.add(parsed)
                }
            }
            i++
        }

        // Group into sets of expectedBallCount
        if (numbers.isEmpty()) return emptyList()

        val results = mutableListOf<String>()
        val chunked = numbers.chunked(expectedBallCount)
        for (chunk in chunked) {
            if (chunk.isNotEmpty()) {
                results.add(chunk.joinToString(","))
            }
        }
        return results
    }
}
