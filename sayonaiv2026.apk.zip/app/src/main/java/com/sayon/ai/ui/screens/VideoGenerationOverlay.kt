package com.sayon.ai.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sayon.ai.ai.AiVideoEngine
import com.sayon.ai.data.SayonRepository
import com.sayon.ai.notifications.SayonNotificationManager
import kotlinx.coroutines.launch

@Composable
fun VideoGenerationOverlay(
    isVisible: Boolean,
    onClose: () -> Unit,
    onVideoGenerated: (videoId: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val aiEngine = remember { AiVideoEngine() }

    var promptText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var progressPercentage by remember { mutableIntStateOf(0) }
    var statusText by remember { mutableStateOf("") }
    var violationErrorText by remember { mutableStateOf<String?>(null) }
    var attachedFileUri by remember { mutableStateOf<String?>(null) }

    // Smooth Gradual Fade-In Black Screen Overlay (Image 1 specification)
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 600)),
        exit = fadeOut(animationSpec = tween(durationMillis = 400))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(enabled = !isGenerating) { onClose() },
            contentAlignment = Alignment.Center
        ) {
            // Close Button Top Right
            IconButton(
                onClick = { if (!isGenerating) onClose() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextPrimary
                )
            }

            // Central Generation Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable(enabled = false) {}, // Prevent dismiss when tapping input
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isGenerating) {
                    // Real-Time Progress Indicator
                    CircularProgressIndicator(
                        progress = { progressPercentage / 100f },
                        modifier = Modifier.size(64.dp),
                        color = GlowCyan,
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "$progressPercentage%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                } else {

                    // Violation Alert Message ("طالب مخالف")
                    violationErrorText?.let { error ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x33FF4D4D)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                                .border(1.dp, ErrorRed, RoundedCornerShape(12.dp))
                        ) {
                            Text(
                                text = "⚠️ $error",
                                color = ErrorRed,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(14.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Floating Glass Input Container matching Reference Image 1
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(34.dp)),
                        shape = RoundedCornerShape(34.dp),
                        color = Color(0xFF14161E)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left Attachment Clip Button (📎 White Circle Button as in Image 1)
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable {
                                        attachedFileUri = "sample_attachment"
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Attach File",
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Central Text Field for Prompt
                            TextField(
                                value = promptText,
                                onValueChange = {
                                    promptText = it
                                    violationErrorText = null
                                },
                                placeholder = {
                                    Text(
                                        text = "اكتب وصف الفيديو هنا... (18 ثانية)",
                                        color = TextHint,
                                        fontSize = 14.sp
                                    )
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Right Arrow Action Button (Image 1 translucent rounded circle arrow)
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                                    .border(1.dp, Color(0x44FFFFFF), CircleShape)
                                    .clickable {
                                        if (promptText.isNotBlank()) {
                                            isGenerating = true
                                            violationErrorText = null

                                            coroutineScope.launch {
                                                aiEngine.generateVideo(promptText, attachedFileUri).collect { state ->
                                                    when (state) {
                                                        is AiVideoEngine.GenerationState.Progress -> {
                                                            progressPercentage = state.percentage
                                                            statusText = state.currentStepDescription
                                                        }
                                                        is AiVideoEngine.GenerationState.Violation -> {
                                                            isGenerating = false
                                                            val reason = state.result.reasonAr
                                                            violationErrorText = reason
                                                            // Trigger violation system notification
                                                            SayonNotificationManager.sendViolationNotification(context, reason)
                                                        }
                                                        is AiVideoEngine.GenerationState.Completed -> {
                                                            isGenerating = false
                                                            val output = state.videoOutput

                                                            // Save to repository feed
                                                            SayonRepository.addGeneratedVideo(
                                                                prompt = output.prompt,
                                                                videoUrl = output.videoUrl,
                                                                thumbnailUrl = output.thumbnailUrl
                                                            )

                                                            // Send completion system notification
                                                            SayonNotificationManager.sendVideoReadyNotification(
                                                                context,
                                                                output.id,
                                                                output.prompt
                                                            )

                                                            promptText = ""
                                                            onVideoGenerated(output.id)
                                                            onClose()
                                                        }
                                                        is AiVideoEngine.GenerationState.Error -> {
                                                            isGenerating = false
                                                            violationErrorText = state.message
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Generate",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (attachedFileUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "📎 تم إرفاق صورة مرجعية للنموذج",
                            color = GlowCyan,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
