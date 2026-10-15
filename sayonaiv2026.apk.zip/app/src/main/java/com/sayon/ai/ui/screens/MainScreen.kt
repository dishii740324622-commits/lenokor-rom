package com.sayon.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sayon.ai.data.SayonRepository
import com.sayon.ai.data.User
import com.sayon.ai.ui.theme.*

@Composable
fun MainScreen(
    initialVideoId: String? = null,
    onLogout: () -> Unit
) {
    var selectedBottomTab by remember { mutableIntStateOf(0) } // 0: Feed, 1: Profile
    var isGenerationOverlayVisible by remember { mutableStateOf(false) }
    var selectedOtherUser by remember { mutableStateOf<User?>(null) }
    var deepLinkVideoId by remember { mutableStateOf(initialVideoId) }

    val currentUser by SayonRepository.currentUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Content Area
        when {
            deepLinkVideoId != null -> {
                VideoDetailScreen(
                    videoId = deepLinkVideoId!!,
                    onBackClick = { deepLinkVideoId = null },
                    onAuthorClick = { user -> selectedOtherUser = user }
                )
            }
            selectedOtherUser != null -> {
                // Viewing Other User Profile
                ProfileScreen(
                    user = selectedOtherUser!!,
                    onVideoClick = { post -> deepLinkVideoId = post.id },
                    onSettingsClick = {},
                    onLogoutClick = {}
                )
            }
            selectedBottomTab == 0 -> {
                // Home Feed Screen
                FeedScreen(
                    onAuthorClick = { user ->
                        if (!user.isCurrentUser) {
                            selectedOtherUser = user
                        } else {
                            selectedBottomTab = 1
                        }
                    }
                )
            }
            selectedBottomTab == 1 -> {
                // Own Profile Screen
                currentUser?.let { me ->
                    ProfileScreen(
                        user = me,
                        onVideoClick = { post -> deepLinkVideoId = post.id },
                        onSettingsClick = { /* Settings */ },
                        onLogoutClick = onLogout
                    )
                }
            }
        }

        // Bottom Navigation Bar matching Reference Image 2
        if (deepLinkVideoId == null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(DarkSurface),
                color = DarkSurface
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Home Tab Icon
                    IconButton(onClick = {
                        selectedOtherUser = null
                        selectedBottomTab = 0
                    }) {
                        Icon(
                            imageVector = if (selectedBottomTab == 0 && selectedOtherUser == null) Icons.Default.Home else Icons.Outlined.Home,
                            contentDescription = "Home",
                            tint = if (selectedBottomTab == 0 && selectedOtherUser == null) GlowWhite else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Plus Generate Center Icon (+)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0x66FFFFFF), CircleShape)
                            .clickable { isGenerationOverlayVisible = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Generate Video",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Profile / Settings Tab Icon
                    IconButton(onClick = {
                        selectedOtherUser = null
                        selectedBottomTab = 1
                    }) {
                        Icon(
                            imageVector = if (selectedBottomTab == 1 && selectedOtherUser == null) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = "Profile",
                            tint = if (selectedBottomTab == 1 && selectedOtherUser == null) GlowWhite else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Smooth Gradual Fade-In Video Generation Overlay Sheet (Image 1 design)
        VideoGenerationOverlay(
            isVisible = isGenerationOverlayVisible,
            onClose = { isGenerationOverlayVisible = false },
            onVideoGenerated = { videoId ->
                deepLinkVideoId = videoId
            }
        )
    }
}
