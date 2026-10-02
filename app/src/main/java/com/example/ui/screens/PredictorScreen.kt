package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AstroCelestialState
import com.example.model.AstroPrediction
import com.example.model.LotteryGame
import com.example.model.ParsedDraw
import com.example.model.ZodiacSign
import com.example.network.ScrapedDrawItem
import com.example.ui.components.BallType
import com.example.ui.components.LotteryBall
import com.example.ui.theme.AstralGreen
import com.example.ui.theme.AstralHotRed
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CelestialPurple
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.CosmicStarWhite
import com.example.ui.theme.CosmicSurfaceElevated
import com.example.ui.theme.CosmicSurfaceNavy
import com.example.ui.theme.PowerballRed

@Composable
fun PredictorScreen(
    selectedGame: LotteryGame,
    rawInputText: String,
    selectedZodiac: ZodiacSign,
    parsedDraws: List<ParsedDraw>,
    predictions: List<AstroPrediction>,
    isGenerating: Boolean,
    celestialState: AstroCelestialState,
    isScraping: Boolean,
    lastScrapeStatus: String?,
    scrapedDrawItems: List<ScrapedDrawItem>,
    onFetchPALotteryClick: () -> Unit,
    onGameChange: (LotteryGame) -> Unit,
    onInputChange: (String) -> Unit,
    onZodiacChange: (ZodiacSign) -> Unit,
    onVoiceInputClick: () -> Unit,
    onGenerateClick: () -> Unit,
    onLoadPresetsClick: () -> Unit,
    onClearInputClick: () -> Unit,
    onSavePrediction: (AstroPrediction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var zodiacMenuExpanded by remember { mutableStateOf(false) }

    val validCount = parsedDraws.count { it.isValid }
    val invalidCount = parsedDraws.size - validCount

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App / Game Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pennsylvania Powerball",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CosmicStarWhite,
                            modifier = Modifier.testTag("app_title_text")
                        )
                        Text(
                            text = "Astro Celestial Predictor",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = CelestialGold
                        )
                    }

                    // Zodiac Pill Selector
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CosmicSurfaceElevated,
                            border = BorderStroke(1.dp, CelestialGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable { zodiacMenuExpanded = true }
                                .testTag("zodiac_selector_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${selectedZodiac.symbol} ${selectedZodiac.signName}",
                                    color = CelestialGold,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Zodiac Sign",
                                    tint = CelestialGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = zodiacMenuExpanded,
                            onDismissRequest = { zodiacMenuExpanded = false },
                            modifier = Modifier.background(CosmicSurfaceElevated)
                        ) {
                            ZodiacSign.values().forEach { sign ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${sign.symbol} ${sign.signName} (${sign.element})", color = CosmicStarWhite)
                                    },
                                    onClick = {
                                        onZodiacChange(sign)
                                        zodiacMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dedicated PA Powerball Format Indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PowerballRed.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, PowerballRed.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("powerball_format_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(PowerballRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "5 White Balls (1-69) + 1 Red Powerball (1-26)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CosmicStarWhite
                        )
                    }
                }
            }
        }

        // Live Running Astrological Clock & Celestial State HUD
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
            border = BorderStroke(1.dp, CelestialGold.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("live_astro_clock_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = CelestialGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE ASTROLOGICAL CLOCK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = CelestialGold
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = AstralGreen.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, AstralGreen)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(AstralGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "RUNNING",
                                color = AstralGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Current running date and time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = celestialState.formattedTime,
                            fontSize = 21.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = CosmicStarWhite
                        )
                        Text(
                            text = celestialState.formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Next PA Powerball Countdown
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicDeepNavy,
                        border = BorderStroke(1.dp, CosmicBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Next PA Powerball Draw:",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = celestialState.nextPowerballCountdown,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Celestial transit indicators row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CelestialInfoChip(
                        icon = celestialState.lunarPhaseSymbol,
                        title = "Moon Phase",
                        value = "${celestialState.lunarPhaseName} (${celestialState.lunarIlluminationPct}%)"
                    )
                    CelestialInfoChip(
                        icon = "🪐",
                        title = "Planetary Hour",
                        value = "${celestialState.planetaryHourRuler} Hour"
                    )
                    CelestialInfoChip(
                        icon = "☀️",
                        title = "Day Ruler",
                        value = "${celestialState.planetaryDayRuler} Day"
                    )
                    CelestialInfoChip(
                        icon = celestialState.currentSunSignSymbol,
                        title = "Sun Season",
                        value = celestialState.currentSunSign
                    )
                }
            }
        }

        // Pennsylvania Lottery Scraper Card (Compact half-height strip)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceElevated),
            border = BorderStroke(1.dp, PowerballRed.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("pa_lottery_scraper_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(PowerballRed.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = PowerballRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "PA Scraper",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CosmicStarWhite
                            )
                            Surface(
                                shape = CircleShape,
                                color = AstralGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, AstralGreen.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = AstralGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        if (scrapedDrawItems.isNotEmpty()) {
                            val latest = scrapedDrawItems.first()
                            Text(
                                text = "${latest.drawDate}: ${latest.whiteBalls.joinToString(" ")} [${latest.powerball}]",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CelestialGold
                            )
                        } else {
                            Text(
                                text = "Official PA Powerball Feed",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Button(
                    onClick = onFetchPALotteryClick,
                    enabled = !isScraping,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PowerballRed,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("auto_fetch_pa_lottery_button")
                ) {
                    if (isScraping) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scraping", fontSize = 11.sp)
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-Scrape", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Enter previous drawings
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Enter previous drawings (one line each):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CosmicStarWhite,
                    modifier = Modifier.testTag("input_section_title")
                )

                Text(
                    text = "Format: 1,15,23,37,61,18",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = CelestialCyan,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Large Input Box
                OutlinedTextField(
                    value = rawInputText,
                    onValueChange = onInputChange,
                    minLines = 5,
                    maxLines = 10,
                    placeholder = {
                        Text(
                            text = "1,15,23,37,61,18\n7,11,19,53,68,23\n...",
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = CosmicStarWhite,
                        lineHeight = 22.sp
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CosmicDeepNavy,
                        unfocusedContainerColor = CosmicDeepNavy,
                        focusedBorderColor = CelestialGold,
                        unfocusedBorderColor = CosmicBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("previous_drawings_input_area")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Validation status & Quick Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (validCount > 0) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AstralGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$validCount valid draw${if (validCount > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelMedium,
                                color = AstralGreen
                            )
                        }
                        if (invalidCount > 0) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = AstralHotRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$invalidCount invalid line${if (invalidCount > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelMedium,
                                color = AstralHotRed
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextButton(
                            onClick = onLoadPresetsClick,
                            modifier = Modifier.testTag("load_presets_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sample Draws", fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = onClearInputClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("clear_input_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear input",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Voice Input & Generate Predictions (Matching Screenshot Exactly)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // [Voice Input] Button
            OutlinedButton(
                onClick = onVoiceInputClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CosmicStarWhite,
                    containerColor = CosmicSurfaceElevated
                ),
                border = BorderStroke(1.5.dp, CelestialCyan.copy(alpha = 0.8f)),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("voice_input_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = CelestialCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Voice Input",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // [Generate Predictions] Button
            Button(
                onClick = onGenerateClick,
                enabled = !isGenerating,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CelestialGold,
                    contentColor = Color(0xFF1E1500)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("generate_predictions_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.Black,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aligning...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate Predictions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section: Predictions Output
        if (predictions.isNotEmpty()) {
            PredictionsOutputSection(
                predictions = predictions,
                selectedGame = selectedGame,
                onSavePrediction = onSavePrediction,
                onCopyTicket = { ticketText ->
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Astro Prediction", ticketText))
                }
            )
        } else {
            // Empty state placeholder
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy.copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, CosmicBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CelestialGold.copy(alpha = 0.6f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Awaiting Celestial Alignment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CosmicStarWhite
                    )
                    Text(
                        text = "Tap 'Auto-Scrape PA Draws' or enter numbers above, then tap 'Generate Predictions'.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun CelestialInfoChip(
    icon: String,
    title: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CosmicDeepNavy,
        border = BorderStroke(1.dp, CosmicBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 16.sp)
            Column {
                Text(text = title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CosmicStarWhite)
            }
        }
    }
}

@Composable
fun PredictionsOutputSection(
    predictions: List<AstroPrediction>,
    selectedGame: LotteryGame,
    onSavePrediction: (AstroPrediction) -> Unit,
    onCopyTicket: (String) -> Unit
) {
    val primary = predictions.firstOrNull() ?: return
    val secondarySets = predictions.drop(1)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = CelestialGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Astro Prediction Forecast",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicStarWhite,
                    modifier = Modifier.testTag("predictions_output_heading")
                )
            }
            Surface(
                shape = CircleShape,
                color = AstralGreen.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, AstralGreen)
            ) {
                Text(
                    text = "${primary.astralResonance}% Resonance",
                    color = AstralGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Hero Primary Prediction Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceElevated),
            border = BorderStroke(1.5.dp, CelestialGold.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("primary_prediction_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "★ PRIMARY TICKET PICK",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = CelestialGold
                    )
                    Text(
                        text = primary.strategyName,
                        style = MaterialTheme.typography.labelSmall,
                        color = CelestialCyan
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Lottery Balls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 5 Main Balls
                    primary.primaryBalls.forEachIndexed { index, number ->
                        LotteryBall(
                            number = number,
                            type = BallType.WHITE,
                            size = 46.dp,
                            animateEntrance = true,
                            delayMs = index * 100,
                            modifier = Modifier.testTag("primary_ball_$index")
                        )
                    }

                    // Special Ball (Powerball)
                    LotteryBall(
                        number = primary.specialBall,
                        type = BallType.POWERBALL,
                        size = 48.dp,
                        animateEntrance = true,
                        delayMs = primary.primaryBalls.size * 100,
                        modifier = Modifier.testTag("primary_special_ball")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Pills Row (Sum, Odd/Even, High/Low)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricPill(label = "Sum Total", value = primary.sumTotal.toString())
                    MetricPill(label = "Odd/Even", value = primary.oddEvenRatio)
                    MetricPill(label = "High/Low", value = primary.highLowRatio)
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CosmicBorder.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(12.dp))

                // Celestial Aspect Explanation
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CelestialPurple,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = primary.astrologicalAspect,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CosmicStarWhite
                        )
                        Text(
                            text = primary.celestialQuote,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: Save & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val ticketString = "${primary.primaryBalls.joinToString(", ")}, ${selectedGame.specialBallName}: ${primary.specialBall}"

                    OutlinedButton(
                        onClick = { onCopyTicket(ticketString) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("copy_primary_ticket_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { onSavePrediction(primary) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = Color.Black),
                        modifier = Modifier.testTag("save_primary_ticket_button")
                    ) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Ticket", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Secondary Prediction Sets Carousel / List
        if (secondarySets.isNotEmpty()) {
            Text(
                text = "Alternative Astral Strategies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CosmicStarWhite
            )

            secondarySets.forEachIndexed { idx, set ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
                    border = BorderStroke(1.dp, CosmicBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("secondary_prediction_card_$idx")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = set.strategyName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CelestialCyan
                            )
                            Text(
                                text = "${set.astralResonance}% Match",
                                fontSize = 12.sp,
                                color = AstralGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            set.primaryBalls.forEach { ballNum ->
                                LotteryBall(
                                    number = ballNum,
                                    type = BallType.WHITE,
                                    size = 38.dp
                                )
                            }
                            LotteryBall(
                                number = set.specialBall,
                                type = BallType.POWERBALL,
                                size = 40.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sum: ${set.sumTotal} • ${set.oddEvenRatio}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            IconButton(
                                onClick = { onSavePrediction(set) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Save set",
                                    tint = CelestialGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CosmicDeepNavy,
        border = BorderStroke(1.dp, CosmicBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicStarWhite
            )
        }
    }
}
