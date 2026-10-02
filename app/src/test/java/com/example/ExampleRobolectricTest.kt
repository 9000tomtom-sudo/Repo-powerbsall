package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.AstroPredictorEngine
import com.example.engine.VoiceInputParser
import com.example.model.LotteryGame
import com.example.model.ZodiacSign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Astro Predictor", appName)
    }

    @Test
    fun `voice input parser handles spoken numbers`() {
        val spoken = "one fifteen twenty-three thirty-seven sixty-one powerball eighteen"
        val parsed = VoiceInputParser.parseSpokenTextToDraw(spoken, expectedBallCount = 6)
        assertTrue(parsed.isNotEmpty())
        assertEquals("1,15,23,37,61,18", parsed[0])
    }

    @Test
    fun `astro predictor engine parses and generates predictions`() {
        val input = "1,15,23,37,61,18\n7,11,19,53,68,23"
        val parsed = AstroPredictorEngine.parseInputDraws(input, LotteryGame.PA_POWERBALL)
        assertEquals(2, parsed.size)
        assertTrue(parsed.all { it.isValid })

        val stats = AstroPredictorEngine.calculateStats(parsed, LotteryGame.PA_POWERBALL)
        assertNotNull(stats)
        assertEquals(2, stats.totalDraws)

        val predictions = AstroPredictorEngine.generatePredictions(
            validDraws = parsed,
            game = LotteryGame.PA_POWERBALL,
            zodiacSign = ZodiacSign.LEO,
            stats = stats
        )
        assertTrue(predictions.isNotEmpty())
        assertEquals(5, predictions[0].primaryBalls.size)
        assertTrue(predictions[0].specialBall in 1..26)
    }

    @Test
    fun `astro clock engine computes live running astronomical state`() {
        val state = com.example.engine.AstroClockEngine.calculateCelestialState(System.currentTimeMillis())
        assertNotNull(state)
        assertTrue(state.formattedTime.isNotBlank())
        assertTrue(state.formattedDate.isNotBlank())
        assertTrue(state.currentSunSign.isNotBlank())
        assertTrue(state.planetaryDayRuler.isNotBlank())
        assertTrue(state.planetaryHourRuler.isNotBlank())
        assertTrue(state.lunarPhaseName.isNotBlank())
        assertTrue(state.lunarIlluminationPct in 0..100)
        assertTrue(state.nextPowerballCountdown.isNotBlank())
    }
}
