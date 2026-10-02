package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DrawingStats
import com.example.model.LotteryGame
import com.example.ui.components.BallType
import com.example.ui.components.LotteryBall
import com.example.ui.theme.AstralColdBlue
import com.example.ui.theme.AstralGreen
import com.example.ui.theme.AstralHotRed
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.CosmicStarWhite
import com.example.ui.theme.CosmicSurfaceElevated
import com.example.ui.theme.CosmicSurfaceNavy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FrequencyAnalyzerScreen(
    stats: DrawingStats?,
    selectedGame: LotteryGame,
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
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = CelestialGold,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Cosmic Frequency & Heatmap",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = CosmicStarWhite
                )
                Text(
                    text = "Historical Drawing Metrics (${stats?.totalDraws ?: 0} Draws Analyzed)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (stats == null || stats.totalDraws == 0) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CelestialCyan,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No drawings available yet",
                        fontWeight = FontWeight.Bold,
                        color = CosmicStarWhite
                    )
                    Text(
                        text = "Enter drawings on the Predictor tab to generate frequency analysis.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            return
        }

        // Hot Numbers Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AstralHotRed.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = null,
                        tint = AstralHotRed
                    )
                    Text(
                        text = "Hot White Balls (Most Frequent)",
                        fontWeight = FontWeight.Bold,
                        color = CosmicStarWhite
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    stats.hotMainBalls.forEach { (number, count) ->
                        NumberFrequencyBadge(
                            number = number,
                            count = count,
                            isHot = true
                        )
                    }
                }
            }
        }

        // Cold / Overdue Numbers Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AstralColdBlue.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = null,
                        tint = AstralColdBlue
                    )
                    Text(
                        text = "Cold / Overdue Numbers (Least Drawn)",
                        fontWeight = FontWeight.Bold,
                        color = CosmicStarWhite
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    stats.coldMainBalls.forEach { (number, count) ->
                        NumberFrequencyBadge(
                            number = number,
                            count = count,
                            isHot = false
                        )
                    }
                }
            }
        }

        // Special Ball (Powerball) Distribution
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${selectedGame.specialBallName} Frequencies",
                    fontWeight = FontWeight.Bold,
                    color = CelestialGold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Hot ${selectedGame.specialBallName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = AstralHotRed
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            stats.hotSpecialBalls.take(2).forEach { (num, cnt) ->
                                NumberFrequencyBadge(number = num, count = cnt, isHot = true, isSpecial = true)
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Cold ${selectedGame.specialBallName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = AstralColdBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            stats.coldSpecialBalls.take(2).forEach { (num, cnt) ->
                                NumberFrequencyBadge(number = num, count = cnt, isHot = false, isSpecial = true)
                            }
                        }
                    }
                }
            }
        }

        // Statistical Distribution Overview
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Astro-Statistical Distribution",
                    fontWeight = FontWeight.Bold,
                    color = CosmicStarWhite
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Average White Ball Sum", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "%.1f".format(stats.averageSum),
                        fontWeight = FontWeight.Bold,
                        color = CelestialCyan
                    )
                }

                HorizontalDivider(color = CosmicBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Dominant Odd Ball Count", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${stats.mostCommonOddCount} Odd / ${selectedGame.mainBallCount - stats.mostCommonOddCount} Even",
                        fontWeight = FontWeight.Bold,
                        color = CelestialGold
                    )
                }

                HorizontalDivider(color = CosmicBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Harmonic Bell Curve Sweet Spot", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "130 – 175",
                        fontWeight = FontWeight.Bold,
                        color = AstralGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun NumberFrequencyBadge(
    number: Int,
    count: Int,
    isHot: Boolean,
    isSpecial: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CosmicDeepNavy,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isHot) AstralHotRed.copy(alpha = 0.6f) else AstralColdBlue.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LotteryBall(
                number = number,
                type = if (isSpecial) BallType.POWERBALL else BallType.WHITE,
                size = 30.dp
            )
            Text(
                text = "${count}x",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHot) AstralHotRed else AstralColdBlue
            )
        }
    }
}
