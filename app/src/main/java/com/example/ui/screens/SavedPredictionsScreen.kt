package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedPredictionEntity
import com.example.ui.components.BallType
import com.example.ui.components.LotteryBall
import com.example.ui.theme.AstralGreen
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicBorder
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.CosmicStarWhite
import com.example.ui.theme.CosmicSurfaceElevated
import com.example.ui.theme.CosmicSurfaceNavy

@Composable
fun SavedPredictionsScreen(
    savedList: List<SavedPredictionEntity>,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var checkDrawInput by remember { mutableStateOf("") }
    var checkWinningResult by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = CelestialGold,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Saved Astral Portfolio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = CosmicStarWhite
                    )
                    Text(
                        text = "${savedList.size} Saved Prediction Tickets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            // Ticket Checker Tool
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, CelestialGold.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAddCheck,
                            contentDescription = null,
                            tint = CelestialGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ticket Win Checker",
                            fontWeight = FontWeight.Bold,
                            color = CosmicStarWhite
                        )
                    }

                    Text(
                        text = "Enter actual winning numbers to test your saved portfolio:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = checkDrawInput,
                        onValueChange = {
                            checkDrawInput = it
                            checkWinningResult = null
                        },
                        placeholder = { Text("e.g. 1,15,23,37,61,18") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ticket_checker_input")
                    )

                    Button(
                        onClick = {
                            val parts = checkDrawInput.split(Regex("[,\\s]+")).mapNotNull { it.toIntOrNull() }
                            if (parts.size >= 6) {
                                val testMain = parts.take(5).toSet()
                                val testSpecial = parts[5]

                                var bestMatchText = "No matches found yet"
                                for (item in savedList) {
                                    val itemMain = item.primaryBalls.split(",").mapNotNull { it.toIntOrNull() }.toSet()
                                    val mainMatches = itemMain.intersect(testMain).size
                                    val specialMatch = item.specialBall == testSpecial
                                    if (mainMatches >= 3 || (mainMatches >= 1 && specialMatch)) {
                                        val tier = when {
                                            mainMatches == 5 && specialMatch -> "🎉 GRAND JACKPOT! 5 Balls + Special Ball!"
                                            mainMatches == 5 -> "🌟 $1,000,000 Match 5!"
                                            mainMatches == 4 && specialMatch -> "✨ $50,000 Match 4 + Special Ball!"
                                            mainMatches == 4 -> "💰 $100 Match 4"
                                            mainMatches == 3 && specialMatch -> "💰 $100 Match 3 + Special Ball"
                                            mainMatches == 3 -> "💵 $7 Match 3"
                                            specialMatch -> "💵 $4 Special Ball Match"
                                            else -> "Partial match: $mainMatches balls"
                                        }
                                        bestMatchText = "$tier on [${item.strategyName}]"
                                        break
                                    }
                                }
                                checkWinningResult = bestMatchText
                            } else {
                                checkWinningResult = "Please enter 5 main balls + 1 special ball"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Check Saved Tickets", fontWeight = FontWeight.Bold)
                    }

                    if (checkWinningResult != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CosmicDeepNavy,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AstralGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = checkWinningResult!!,
                                fontWeight = FontWeight.Bold,
                                color = AstralGreen,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        if (savedList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No saved predictions yet",
                            fontWeight = FontWeight.Bold,
                            color = CosmicStarWhite
                        )
                        Text(
                            text = "Generate predictions and tap 'Save Ticket' to store them in your local portfolio.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(savedList, key = { it.id }) { prediction ->
                SavedTicketCard(
                    item = prediction,
                    onDelete = { onDelete(prediction.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SavedTicketCard(
    item: SavedPredictionEntity,
    onDelete: () -> Unit
) {
    val ballNumbers = item.primaryBalls.split(",").mapNotNull { it.toIntOrNull() }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurfaceNavy),
        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicBorder),
        modifier = Modifier.fillMaxWidth()
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
                Column {
                    Text(
                        text = item.gameName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CelestialGold
                    )
                    Text(
                        text = item.strategyName,
                        style = MaterialTheme.typography.bodySmall,
                        color = CelestialCyan
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ticket",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Balls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ballNumbers.forEach { num ->
                    LotteryBall(
                        number = num,
                        type = BallType.WHITE,
                        size = 38.dp
                    )
                }
                LotteryBall(
                    number = item.specialBall,
                    type = BallType.POWERBALL,
                    size = 40.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sum: ${item.sumTotal} • ${item.astralResonance}% Resonance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
