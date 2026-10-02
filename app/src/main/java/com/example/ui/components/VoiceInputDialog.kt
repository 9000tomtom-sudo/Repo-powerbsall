package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialGold
import com.example.ui.theme.CosmicSurfaceElevated
import com.example.ui.theme.PowerballRed

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onSpeechLaunch: () -> Unit,
    onManualSubmit: (String) -> Unit
) {
    var manualDictationText by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = CelestialGold
                )
                Text("Voice Draw Dictation", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Speak numbers clearly in sequence, for example:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "\"one, fifteen, twenty three, thirty seven, sixty one, eighteen\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = CelestialCyan,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Animated mic launch button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(72.dp)
                        .background(PowerballRed.copy(alpha = 0.15f), CircleShape)
                        .border(2.dp, PowerballRed, CircleShape)
                ) {
                    Button(
                        onClick = onSpeechLaunch,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PowerballRed),
                        modifier = Modifier
                            .size(56.dp)
                            .testTag("start_voice_recognition_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Activate microphone",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tap the mic for system speech or enter words below:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = manualDictationText,
                    onValueChange = { manualDictationText = it },
                    placeholder = { Text("e.g. 5, 14, 28, 39, 62, 11") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_dictation_input_field")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (manualDictationText.isNotBlank()) {
                        onManualSubmit(manualDictationText)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CelestialGold, contentColor = Color.Black),
                modifier = Modifier.testTag("apply_voice_dictation_button")
            ) {
                Text("Apply Numbers")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_voice_dictation_button")
            ) {
                Text("Cancel")
            }
        },
        containerColor = CosmicSurfaceElevated,
        shape = RoundedCornerShape(20.dp)
    )
}
