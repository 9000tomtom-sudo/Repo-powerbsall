package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.VoiceInputDialog
import com.example.ui.screens.CosmicAlmanacScreen
import com.example.ui.screens.FrequencyAnalyzerScreen
import com.example.ui.screens.PredictorScreen
import com.example.ui.screens.SavedPredictionsScreen
import com.example.ui.theme.AstroPredictorTheme
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.CosmicStarWhite
import com.example.ui.theme.CosmicSurfaceNavy
import com.example.ui.viewmodel.AstroPredictorViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstroPredictorTheme(darkTheme = true) {
                AstroPredictorApp()
            }
        }
    }
}

@Composable
fun AstroPredictorApp(
    viewModel: AstroPredictorViewModel = viewModel()
) {
    val selectedGame by viewModel.selectedGame.collectAsStateWithLifecycle()
    val rawInputText by viewModel.rawInputText.collectAsStateWithLifecycle()
    val selectedZodiac by viewModel.selectedZodiac.collectAsStateWithLifecycle()
    val parsedDraws by viewModel.parsedDraws.collectAsStateWithLifecycle()
    val drawingStats by viewModel.drawingStats.collectAsStateWithLifecycle()
    val predictions by viewModel.predictions.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val savedList by viewModel.savedPredictions.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    // Live Astrological Running Clock and Pennsylvania Scraper States
    val celestialState by viewModel.celestialClock.collectAsStateWithLifecycle()
    val isScraping by viewModel.isScraping.collectAsStateWithLifecycle()
    val lastScrapeStatus by viewModel.lastScrapeStatus.collectAsStateWithLifecycle()
    val scrapedDrawItems by viewModel.scrapedDrawItems.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showVoiceModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Android Speech Recognizer launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.appendVoiceSpokenText(spokenText)
            }
        }
    }

    fun launchSystemSpeechRecognizer() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Speak previous drawings (e.g. 1 15 23 37 61 18)"
                )
            }
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            // Fallback: show voice modal
            showVoiceModal = true
        }
    }

    // Handle back press if in secondary tab
    BackHandler(enabled = activeTab != 0) {
        viewModel.setActiveTab(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CosmicDeepNavy,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = CosmicSurfaceNavy,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Predictor") },
                    label = { Text("Predictor") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CelestialGold,
                        indicatorColor = CelestialGold,
                        unselectedIconColor = CosmicStarWhite.copy(alpha = 0.6f),
                        unselectedTextColor = CosmicStarWhite.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_predictor")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Frequency") },
                    label = { Text("Frequency") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CelestialGold,
                        indicatorColor = CelestialGold,
                        unselectedIconColor = CosmicStarWhite.copy(alpha = 0.6f),
                        unselectedTextColor = CosmicStarWhite.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_frequency")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "Portfolio") },
                    label = { Text("Portfolio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CelestialGold,
                        indicatorColor = CelestialGold,
                        unselectedIconColor = CosmicStarWhite.copy(alpha = 0.6f),
                        unselectedTextColor = CosmicStarWhite.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_portfolio")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.setActiveTab(3) },
                    icon = { Icon(Icons.Default.NightsStay, contentDescription = "Almanac") },
                    label = { Text("Almanac") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = CelestialGold,
                        indicatorColor = CelestialGold,
                        unselectedIconColor = CosmicStarWhite.copy(alpha = 0.6f),
                        unselectedTextColor = CosmicStarWhite.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_tab_almanac")
                )
            }
        }
    ) { innerPadding ->
        when (activeTab) {
            0 -> PredictorScreen(
                selectedGame = selectedGame,
                rawInputText = rawInputText,
                selectedZodiac = selectedZodiac,
                parsedDraws = parsedDraws,
                predictions = predictions,
                isGenerating = isGenerating,
                celestialState = celestialState,
                isScraping = isScraping,
                lastScrapeStatus = lastScrapeStatus,
                scrapedDrawItems = scrapedDrawItems,
                onFetchPALotteryClick = { viewModel.fetchPennsylvaniaPowerballDraws(silent = false) },
                onGameChange = { viewModel.setSelectedGame(it) },
                onInputChange = { viewModel.setInputText(it) },
                onZodiacChange = { viewModel.setSelectedZodiac(it) },
                onVoiceInputClick = { showVoiceModal = true },
                onGenerateClick = { viewModel.generatePredictions(showAnimation = true) },
                onLoadPresetsClick = { viewModel.loadPresetDraws() },
                onClearInputClick = { viewModel.clearInput() },
                onSavePrediction = { viewModel.savePrediction(it) },
                modifier = Modifier.padding(innerPadding)
            )
            1 -> FrequencyAnalyzerScreen(
                stats = drawingStats,
                selectedGame = selectedGame,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> SavedPredictionsScreen(
                savedList = savedList,
                onDelete = { viewModel.deleteSavedPrediction(it) },
                modifier = Modifier.padding(innerPadding)
            )
            3 -> CosmicAlmanacScreen(
                selectedZodiac = selectedZodiac,
                onSelectZodiac = { viewModel.setSelectedZodiac(it) },
                celestialState = celestialState,
                modifier = Modifier.padding(innerPadding)
            )
        }

        if (showVoiceModal) {
            VoiceInputDialog(
                onDismiss = { showVoiceModal = false },
                onSpeechLaunch = {
                    showVoiceModal = false
                    launchSystemSpeechRecognizer()
                },
                onManualSubmit = { spoken ->
                    viewModel.appendVoiceSpokenText(spoken)
                }
            )
        }
    }
}
