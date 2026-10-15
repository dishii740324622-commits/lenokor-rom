package com.sayon.ai.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.sayon.ai.data.SayonRepository
import com.sayon.ai.data.User
import com.sayon.ai.data.VideoPost
import com.sayon.ai.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    onAuthorClick: (User) -> Unit
) {
    val posts by SayonRepository.videoPosts.collectAsState()
    val currentUser by SayonRepository.currentUser.collectAsState()
    val otherUsers by SayonRepository.otherUsers.collectAsState()

    if (posts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "لا توجد فيديوهات متاحة. أضف أول فيديو بالضغط على +",
                color = TextSecondary,
                fontSize = 15.sp
            )
        }
    } else {
        val pagerState = rememberPagerState(pageCount = { posts.size })

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val post = posts[page]
            val isCurrentCreator = post.authorUserId == currentUser?.id ||
                    (currentUser != null && post.authorUserId.startsWith("user_me_"))

            val authorUser = if (isCurrentCreator) {
                currentUser
            } else {
                otherUsers[post.authorUserId] ?: User(
                    id = post.authorUserId,
                    name = post.authorUsername,
                    username = post.authorUsername,
                    email = "",
                    avatarUrl = post.authorAvatarUrl
                )
            }

            VideoItemView(
                post = post,
                isCurrentCreator = isCurrentCreator,
                authorUser = authorUser,
                onAuthorClick = { authorUser?.let { onAuthorClick(it) } }
            )
        }
    }
}

import com.sayon.ai.ui.components.VideoWatermarkOverlay

@Composable
fun VideoItemView(
    post: VideoPost,
    isCurrentCreator: Boolean,
    authorUser: User?,
    onAuthorClick: () -> Unit
) {
    val context = LocalContext.current
    var showCommentsSheet by remember { mutableStateOf(false) }
    var showMenuDropdown by remember { mutableStateOf(false) }

    // ExoPlayer Video Player Lifecycle Setup
    val exoPlayer = remember(context, post.videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(post.videoUrl))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video Player View (Media3 ExoPlayer)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Animated Dynamic Watermark (Cycles position every 3 seconds: Bottom-Left -> Top-Left -> Top-Right)
        VideoWatermarkOverlay()

        // Overlay Details Bottom Left (Author, Avatar, Bio Prompt)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp, end = 90.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAuthorClick() }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = post.authorAvatarUrl),
                    contentDescription = post.authorUsername,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.dp, GlowWhite, CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = post.authorUsername,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                if (authorUser?.isVerified == true) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                        contentDescription = "Verified Creator",
                        tint = GlowCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = post.prompt,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 18s Duration Badge
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "✨ 18s Sayon AI Model",
                    color = GlowCyan,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Overlay Action Icons Right Bar (Likes, Comments, 3-Dots Menu)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { SayonRepository.toggleLike(post.id) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = if (post.isLikedByCurrentUser) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByCurrentUser) Color.Red else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = com.sayon.ai.data.formatCompactNumber(post.likesCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comments Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showCommentsSheet = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = com.sayon.ai.data.formatCompactNumber(post.commentsCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 3-Dots Options Menu Button (⋮)
            Box {
                IconButton(
                    onClick = { showMenuDropdown = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = showMenuDropdown,
                    onDismissRequest = { showMenuDropdown = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    // Option 1: Copy Video Link (For Everyone)
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("نسخ رابط الفيديو", color = TextPrimary, fontSize = 14.sp)
                            }
                        },
                        onClick = {
                            showMenuDropdown = false
                            val videoLink = "sayonai://video/${post.id}"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Sayon AI Video Link", videoLink)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ رابط الفيديو إلى الحافظة! 🔗", Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Option 2: Delete Video (For Creator Only)
                    if (isCurrentCreator) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("حذف الفيديو", color = ErrorRed, fontSize = 14.sp)
                                }
                            },
                            onClick = {
                                showMenuDropdown = false
                                SayonRepository.deleteVideo(post.id)
                                Toast.makeText(context, "تم حذف الفيديو بنجاح", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Comments Bottom Sheet
        if (showCommentsSheet) {
            CommentsBottomSheet(
                post = post,
                onDismiss = { showCommentsSheet = false }
            )
        }
    }
}
