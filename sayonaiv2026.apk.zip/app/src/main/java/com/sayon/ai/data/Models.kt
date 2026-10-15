package com.sayon.ai.data

data class User(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val avatarUrl: String,
    val bio: String = "أهلاً بكم في حسابي الشخصي على Sayon AI 🎬✨",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isCurrentUser: Boolean = false,
    val isFollowing: Boolean = false
) {
    // Automatically verified if followers count reaches 100,000 (100K+)
    val isVerified: Boolean get() = followersCount >= 100_000
}

data class VideoPost(
    val id: String,
    val authorUserId: String,
    val authorUsername: String,
    val authorAvatarUrl: String,
    val prompt: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int = 18,
    var likesCount: Int = 0,
    var commentsCount: Int = 0,
    var isLikedByCurrentUser: Boolean = false,
    val isPrivate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Comment(
    val id: String,
    val videoId: String,
    val userId: String,
    val username: String,
    val userAvatar: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Helper utility to format counts into compact notation (e.g. 1K, 100K, 2.5M)
 */
fun formatCompactNumber(count: Int): String {
    return when {
        count >= 1_000_000 -> {
            val value = count / 1_000_000.0
            if (value % 1.0 == 0.0) "${value.toInt()}M" else "%.1fM".format(value)
        }
        count >= 1_000 -> {
            val value = count / 1_000.0
            if (value % 1.0 == 0.0) "${value.toInt()}K" else "%.1fK".format(value)
        }
        else -> count.toString()
    }
}
