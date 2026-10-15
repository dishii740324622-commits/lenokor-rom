package com.sayon.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.sayon.ai.data.SayonRepository
import com.sayon.ai.notifications.SayonNotificationManager
import com.sayon.ai.ui.screens.AuthScreen
import com.sayon.ai.ui.screens.MainScreen
import com.sayon.ai.ui.theme.SayonAITheme

import android.view.WindowManager

class MainActivity : ComponentActivity() {

    private var initialVideoId: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission result */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Block screen recording and screenshots across the entire application
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Initialize Notification Channels
        SayonNotificationManager.createNotificationChannel(this)

        // Request Notification Permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Process Deep Link Intent or Notification Intent
        handleIncomingIntent(intent)

        setContent {
            SayonAITheme {
                val currentUser by SayonRepository.currentUser.collectAsState()

                if (currentUser == null) {
                    AuthScreen(
                        onLoginSuccess = {
                            // User logged in
                        }
                    )
                } else {
                    MainScreen(
                        initialVideoId = initialVideoId,
                        onLogout = {
                            SayonRepository.logout()
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        intent?.let {
            // Notification Extra Video ID
            val extraVideoId = it.getStringExtra("EXTRA_VIDEO_ID")
            if (extraVideoId != null) {
                initialVideoId = extraVideoId
                return
            }

            // Deep Link sayonai://video/{videoId}
            val data = it.data
            if (data != null && data.scheme == "sayonai" && data.host == "video") {
                val videoId = data.lastPathSegment
                if (!videoId.isNull_or_empty()) {
                    initialVideoId = videoId
                }
            }
        }
    }
}

private fun String.isNull_or_empty(): Boolean = this.isEmpty()
