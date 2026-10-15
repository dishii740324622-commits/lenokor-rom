package com.sayon.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sayon.ai.data.SayonRepository
import com.sayon.ai.data.User
import com.sayon.ai.ui.theme.DarkBackground
import com.sayon.ai.ui.theme.TextPrimary
import com.sayon.ai.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoId: String,
    onBackClick: () -> Unit,
    onAuthorClick: (User) -> Unit
) {
    val post = SayonRepository.getPostById(videoId)
    val currentUser = SayonRepository.currentUser.value
    val otherUsers = SayonRepository.otherUsers.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (post != null) {
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

            // Top Bar Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "عذراً، الفيديو غير موجود أو تم حذفه.", color = TextSecondary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) {
                    Text(text = "العودة للرئيسية")
                }
            }
        }
    }
}
