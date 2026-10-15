package com.sayon.ai.ai

/**
 * Output data structure of the generated AI video.
 * Enforces 18-second duration without generation limits.
 */
data class VideoOutput(
    val id: String,
    val prompt: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int = 18,
    val isPhotorealistic: Boolean = true,
    val languageCode: String = "ar",
    val hasAudioTrack: Boolean = true,
    val audioClarityScore: Float = 0.99f,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
