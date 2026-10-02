package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AstroCelestialState
import com.example.model.ZodiacSign
import com.example.ui.components.BallType
import com.example.ui.components.LotteryBall
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CelestialPurple
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.CosmicStarWhite
import com.example.ui.theme.CosmicSurfaceElevated
import com.example.ui.theme.CosmicSurfaceNavy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CosmicAlmanacScreen(
    selectedZodiac: ZodiacSign,
    onSelectZodiac: (ZodiacSign) -> Unit,
    celestialState: AstroCelestialState? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NightsStay,
                contentDescription = null,
                tint = CelestialPurple,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Cosmic Astral Almanac",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = CosmicStarWhite
                )
                Text(
                    text = if (celestialState != null) "${celestialState.formattedDate} • ${celestialState.formattedTime}" else "Planetary Transits & Zodiac Lucky Numbers",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Transit Snapshot Card
        if (celestialState != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, CelestialGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Active Celestial Coordinates",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CelestialGold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Lunar Phase:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(
                            text = "${celestialState.lunarPhaseSymbol} ${celestialState.lunarPhaseName} (${celestialState.lunarIlluminationPct}%)",
                            fontWeight = FontWeight.Bold,
                            color = CosmicStarWhite,
                            fontSize = 13.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Planetary Day & Hour:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(
                            text = "${celestialState.planetaryDayRuler} Day / ${celestialState.planetaryHourRuler} Hour",
                            fontWeight = FontWeight.Bold,
                            color = CelestialCyan,
                            fontSize = 13.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sun Sign Season:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(
                            text = "${celestialState.currentSunSignSymbol} ${celestialState.currentSunSign}",
                            fontWeight = FontWeight.Bold,
                            color = CelestialGold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Active Sign Detail Hero Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CelestialGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CelestialGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = selectedZodiac.symbol,
                                    fontSize = 24.sp,
                                    color = CelestialGold
                                )
                            }
                        }
                        Column {
                            Text(
                                text = selectedZodiac.signName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicStarWhite
                            )
                            Text(
                                text = selectedZodiac.dates,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CosmicDeepNavy,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder)
                    ) {
                        Text(
                            text = "${selectedZodiac.element} Element",
                            color = CelestialCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Ruling Planet", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = selectedZodiac.rulingPlanet, fontWeight = FontWeight.Bold, color = CelestialGold)
                    }
                    Column {
                        Text(text = "Cosmic Vibration", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = selectedZodiac.cosmicVibe, fontWeight = FontWeight.Bold, color = CosmicStarWhite)
                    }
                }

                Text(
                    text = "Innate Sacred Lucky Balls:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    selectedZodiac.luckyNumbers.forEach { num ->
                        LotteryBall(
                            number = num,
                            type = BallType.GOLD,
                            size = 36.dp
                        )
                    }
                }
            }
        }

        // All 12 Signs Grid / Selector
        Text(
            text = "Select Your Cosmic Sign",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CosmicStarWhite
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ZodiacSign.values().forEach { sign ->
                val isSelected = sign == selectedZodiac
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) CelestialGold else CosmicSurfaceNavy,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CelestialGold else CosmicBorder
                    ),
                    modifier = Modifier
                        .clickable { onSelectZodiac(sign) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = sign.symbol,
                            fontSize = 16.sp,
                            color = if (isSelected) Color.Black else CelestialGold
                        )
                        Text(
                            text = sign.signName,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else CosmicStarWhite
                        )
                    }
                }
            }
        }

        // Planetary Transit Outlook
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = CelestialGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Current Planetary Hours & Lottery Wisdom",
                        fontWeight = FontWeight.Bold,
                        color = CosmicStarWhite
                    )
                }
                Text(
                    text = "• Jupiter in direct transit enhances broad-spectrum high numbers (45-69).\n" +
                           "• Mercury in conjunction with lunar cycle sharpens number gaps (Delta pairs).\n" +
                           "• The optimal celestial balance for Pennsylvania Powerball is 3 Odd / 2 Even with sum between 130 and 175.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
