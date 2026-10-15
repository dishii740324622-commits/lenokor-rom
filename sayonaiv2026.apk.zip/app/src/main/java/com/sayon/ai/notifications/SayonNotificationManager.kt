package com.sayon.ai.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sayon.ai.MainActivity

object SayonNotificationManager {

    private const val CHANNEL_ID = "sayon_ai_gen_channel"
    private const val CHANNEL_NAME = "تنبيهات Sayon AI"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات توليد الفيديوهات والتنبيهات الأمنية"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Triggered when AI video generation is completed successfully.
     */
    fun sendVideoReadyNotification(context: Context, videoId: String, prompt: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_VIDEO_ID", videoId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_slideshow)
            .setContentTitle("تم اكتمال فيديو الذكاء الاصطناعي! ✨")
            .setContentText("تعال وشاهد الفيديو الخاص بك الآن 🎬")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("تم اكتمال فيديو الذكاء الاصطناعي بمدة 18 ثانية وبأصوات واقعية!\nالوصف: $prompt\nاضغط للفتح والمشاهدة.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    /**
     * Triggered when prompt violates safety moderation rules.
     */
    fun sendViolationNotification(context: Context, reason: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            102,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("تنبيه: طلب مخالف لسياسة الاستخدام ⚠️")
            .setContentText(reason)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
