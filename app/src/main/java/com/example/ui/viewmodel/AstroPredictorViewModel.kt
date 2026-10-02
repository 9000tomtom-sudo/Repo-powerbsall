package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AstroDatabase
import com.example.data.local.AstroRepository
import com.example.data.local.SavedPredictionEntity
import com.example.engine.AstroCelestialState
import com.example.engine.AstroClockEngine
import com.example.engine.AstroPredictorEngine
import com.example.engine.VoiceInputParser
import com.example.model.AstroPrediction
import com.example.model.DrawingStats
import com.example.model.LotteryGame
import com.example.model.ParsedDraw
import com.example.model.ZodiacSign
import com.example.network.PennsylvaniaLotteryScraper
import com.example.network.ScrapedDrawItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AstroPredictorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AstroRepository

    init {
        val dao = AstroDatabase.getDatabase(application).astroDao()
        repository = AstroRepository(dao)
    }

    val savedPredictions: StateFlow<List<SavedPredictionEntity>> = repository.savedPredictions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedGame = MutableStateFlow(LotteryGame.PA_POWERBALL)
    val selectedGame: StateFlow<LotteryGame> = _selectedGame.asStateFlow()

    private val _rawInputText = MutableStateFlow(
        LotteryGame.PA_POWERBALL.sampleDraws.joinToString("\n")
    )
    val rawInputText: StateFlow<String> = _rawInputText.asStateFlow()

    private val _selectedZodiac = MutableStateFlow(ZodiacSign.LEO)
    val selectedZodiac: StateFlow<ZodiacSign> = _selectedZodiac.asStateFlow()

    private val _parsedDraws = MutableStateFlow<List<ParsedDraw>>(emptyList())
    val parsedDraws: StateFlow<List<ParsedDraw>> = _parsedDraws.asStateFlow()

    private val _drawingStats = MutableStateFlow<DrawingStats?>(null)
    val drawingStats: StateFlow<DrawingStats?> = _drawingStats.asStateFlow()

    private val _predictions = MutableStateFlow<List<AstroPrediction>>(emptyList())
    val predictions: StateFlow<List<AstroPrediction>> = _predictions.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Real-Time Running Astro Clock State
    private val _celestialClock = MutableStateFlow(AstroClockEngine.calculateCelestialState())
    val celestialClock: StateFlow<AstroCelestialState> = _celestialClock.asStateFlow()

    // Pennsylvania Lottery Scraper States
    private val _isScraping = MutableStateFlow(false)
    val isScraping: StateFlow<Boolean> = _isScraping.asStateFlow()

    private val _lastScrapeStatus = MutableStateFlow<String?>(null)
    val lastScrapeStatus: StateFlow<String?> = _lastScrapeStatus.asStateFlow()

    private val _scrapedDrawItems = MutableStateFlow<List<ScrapedDrawItem>>(emptyList())
    val scrapedDrawItems: StateFlow<List<ScrapedDrawItem>> = _scrapedDrawItems.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        // Start running real-time date/time and astrological calculations
        startLiveCelestialClock()

        recomputeDrawsAndStats()
        generatePredictions(showAnimation = false)

        // Automatically scrape latest live PA Powerball numbers
        fetchPennsylvaniaPowerballDraws(silent = true)
    }

    private fun startLiveCelestialClock() {
        viewModelScope.launch {
            while (isActive) {
                _celestialClock.value = AstroClockEngine.calculateCelestialState(System.currentTimeMillis())
                delay(1000) // Ticking every second
            }
        }
    }

    fun fetchPennsylvaniaPowerballDraws(silent: Boolean = false) {
        viewModelScope.launch {
            _isScraping.value = true
            _lastScrapeStatus.value = "Connecting to Pennsylvania Lottery live feed..."

            val response = PennsylvaniaLotteryScraper.fetchLatestPowerballDraws()
            _isScraping.value = false

            if (response.success && response.formattedLines.isNotEmpty()) {
                _scrapedDrawItems.value = response.items
                _lastScrapeStatus.value = response.message

                // Automatically load scraped drawings into application
                if (_selectedGame.value == LotteryGame.PA_POWERBALL) {
                    _rawInputText.value = response.formattedLines.joinToString("\n")
                    recomputeDrawsAndStats()
                    generatePredictions(showAnimation = false)

                    // Persist to local database
                    repository.saveDraws("PA_POWERBALL", response.formattedLines)
                }

                if (!silent) {
                    emitToast("⚡ Automatically synced ${response.items.size} latest PA Powerball drawings!")
                }
            } else {
                _lastScrapeStatus.value = "Failed to scrape live feed: ${response.message}"
                if (!silent) {
                    emitToast("Could not update live PA drawings. Using offline archive.")
                }
            }
        }
    }

    fun setSelectedGame(game: LotteryGame) {
        _selectedGame.value = game
        _rawInputText.value = game.sampleDraws.joinToString("\n")
        recomputeDrawsAndStats()
        generatePredictions(showAnimation = false)
    }

    fun setInputText(text: String) {
        _rawInputText.value = text
        recomputeDrawsAndStats()
    }

    fun setSelectedZodiac(sign: ZodiacSign) {
        _selectedZodiac.value = sign
        // Regenerate prediction to sync with new sign's astrology
        generatePredictions(showAnimation = false)
    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun loadPresetDraws() {
        val current = _selectedGame.value
        _rawInputText.value = current.sampleDraws.joinToString("\n")
        recomputeDrawsAndStats()
        emitToast("Loaded ${current.displayName} presets")
    }

    fun clearInput() {
        _rawInputText.value = ""
        recomputeDrawsAndStats()
        _predictions.value = emptyList()
        emitToast("Cleared drawings input")
    }

    fun appendVoiceSpokenText(spokenText: String) {
        val game = _selectedGame.value
        val parsedLines = VoiceInputParser.parseSpokenTextToDraw(
            text = spokenText,
            expectedBallCount = game.mainBallCount + 1
        )

        if (parsedLines.isEmpty()) {
            emitToast("Could not recognize lottery numbers from: \"$spokenText\"")
            return
        }

        val existing = _rawInputText.value.trim()
        val addition = parsedLines.joinToString("\n")
        val updated = if (existing.isEmpty()) addition else "$existing\n$addition"
        _rawInputText.value = updated
        recomputeDrawsAndStats()
        emitToast("Voice added ${parsedLines.size} drawing(s)")
    }

    fun recomputeDrawsAndStats() {
        val game = _selectedGame.value
        val parsed = AstroPredictorEngine.parseInputDraws(_rawInputText.value, game)
        _parsedDraws.value = parsed
        _drawingStats.value = AstroPredictorEngine.calculateStats(parsed, game)
    }

    fun generatePredictions(showAnimation: Boolean = true) {
        val game = _selectedGame.value
        recomputeDrawsAndStats()
        val stats = _drawingStats.value ?: return
        val validDraws = _parsedDraws.value.filter { it.isValid }
        val currentCelestial = _celestialClock.value

        viewModelScope.launch {
            if (showAnimation) {
                _isGenerating.value = true
                delay(600) // Pulsing cosmic alignment
            }
            val results = AstroPredictorEngine.generatePredictions(
                validDraws = validDraws,
                game = game,
                zodiacSign = _selectedZodiac.value,
                stats = stats,
                celestialState = currentCelestial
            )
            _predictions.value = results
            _isGenerating.value = false
            if (showAnimation) {
                emitToast("Generated ${results.size} Astro Predictions!")
            }
        }
    }

    fun savePrediction(prediction: AstroPrediction) {
        viewModelScope.launch {
            val entity = SavedPredictionEntity(
                gameName = prediction.game.displayName,
                strategyName = prediction.strategyName,
                primaryBalls = prediction.primaryBalls.joinToString(","),
                specialBall = prediction.specialBall,
                astralResonance = prediction.astralResonance,
                sumTotal = prediction.sumTotal,
                astrologicalAspect = prediction.astrologicalAspect
            )
            repository.savePrediction(entity)
            emitToast("Ticket saved to Astral Portfolio")
        }
    }

    fun deleteSavedPrediction(id: Int) {
        viewModelScope.launch {
            repository.deletePrediction(id)
            emitToast("Prediction removed")
        }
    }

    private fun emitToast(msg: String) {
        viewModelScope.launch {
            _toastEvent.emit(msg)
        }
    }
}
