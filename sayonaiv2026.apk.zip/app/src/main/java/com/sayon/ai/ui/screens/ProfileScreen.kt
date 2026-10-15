package com.sayon.ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.sayon.ai.data.SayonRepository
import com.sayon.ai.data.User
import com.sayon.ai.data.VideoPost
import com.sayon.ai.data.formatCompactNumber
import com.sayon.ai.ui.theme.*

@Composable
fun ProfileScreen(
    user: User,
    onVideoClick: (VideoPost) -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val allPosts by SayonRepository.videoPosts.collectAsState()
    val userPosts = remember(allPosts, user.id) {
        allPosts.filter { it.authorUserId == user.id || (user.isCurrentUser && it.authorUserId.startsWith("user_me_")) }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Videos, 1: Liked, 2: Private

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Header Bar: @user_name [✔️ Verified Badge] | Settings & Notification icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.username,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Verified Badge at 100K+ Followers
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Badge",
                        tint = GlowCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = TextPrimary
                    )
                }
                IconButton(onClick = { /* Notifications */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Profile Avatar, Followers & Following Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Profile Picture with Camera '+' Overlay
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = rememberAsyncImagePainter(model = user.avatarUrl),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(DarkCardSurface)
                        .border(2.dp, DarkGlassBorder, CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(DarkCardSurface)
                        .border(1.dp, TextPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Avatar",
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Followers & Following Stats (Formatted notation: 1K, 100K, 2.4M)
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCompactNumber(user.followersCount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Followers",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatCompactNumber(user.followingCount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Following",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row:
        // CRITICAL RULE: If viewing OWN profile (user.isCurrentUser = true), DO NOT SHOW FOLLOW BUTTON!
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!user.isCurrentUser) {
                // Show Follow Button only for other users
                Button(
                    onClick = { SayonRepository.toggleFollowOtherUser(user.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.isFollowing) DarkCardSurface else Color.White
                    )
                ) {
                    Text(
                        text = if (user.isFollowing) "Unfollow" else "Follow",
                        color = if (user.isFollowing) TextPrimary else Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                // Message Icon Button
                IconButton(
                    onClick = { /* Message */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkCardSurface, RoundedCornerShape(8.dp))
                        .border(1.dp, DarkGlassBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mail,
                        contentDescription = "Message",
                        tint = TextPrimary
                    )
                }
            } else {
                // Own Profile: Show Edit Profile and Sign Out options instead
                Button(
                    onClick = { /* Edit Bio */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .border(1.dp, DarkGlassBorder, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCardSurface)
                ) {
                    Text(text = "تعديل الملف الشخصي", color = TextPrimary, fontSize = 14.sp)
                }

                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .height(40.dp)
                        .border(1.dp, ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "خروج", color = ErrorRed, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bio Text Section
        Text(
            text = user.bio,
            fontSize = 13.sp,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Content Tabs Row: Videos | Liked | Private (Image 2 design)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = DarkGlassBorder)
        ) {
            val tabs = listOf("Videos", "Liked", "Private")
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = index }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(TextPrimary)
                            )
                        }
                    }
                }
            }
        }

        // Grid of Video Thumbnails with Likes & Comments Count
        val currentTabPosts = when (selectedTab) {
            0 -> userPosts
            1 -> allPosts.filter { it.isLikedByCurrentUser }
            else -> userPosts.filter { it.isPrivate }
        }

        if (currentTabPosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == 0) "لا توجد فيديوهات حتى الآن" else "القائمة فارغة",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(currentTabPosts) { post ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(0.8f)
                            .padding(1.dp)
                            .background(DarkCardSurface)
                            .clickable { onVideoClick(post) }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = post.thumbnailUrl),
                            contentDescription = post.prompt,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Bottom Overlay showing Likes & Comments (Formatted K / M)
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Likes",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = formatCompactNumber(post.likesCount),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = "Comments",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = formatCompactNumber(post.commentsCount),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
