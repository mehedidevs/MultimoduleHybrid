package com.mehedi.hybridnavigation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

// NotificationPermissionHandler.kt
class NotificationPermissionHandler(private val activity: ComponentActivity) {

    private val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
            onPermissionGranted?.invoke()
        } else {
            // Permission denied
            onPermissionDenied?.invoke()
        }
    }

    var onPermissionGranted: (() -> Unit)? = null
    var onPermissionDenied: (() -> Unit)? = null

    fun checkNotificationPermission(
        onGranted: () -> Unit = {},
        onShouldShowRationale: () -> Unit = {},
        onDenied: () -> Unit = {}
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                // Permission already granted
                ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted()
                }
                // Should show permission rationale
                activity.shouldShowRequestPermissionRationale(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    onShouldShowRationale()
                }
                // Request permission
                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    onPermissionGranted = { onGranted() }
                    onPermissionDenied = { onDenied() }
                }
            }
        } else {
            // Below Android 13, notification permission is granted by default
            onGranted()
        }
    }
}

// NotificationHelper.kt
class NotificationHelper(private val context: Context) {

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Product Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new products and updates"
            }

            NotificationManagerCompat.from(context)
                .createNotificationChannel(channel)
        }
    }

    fun showProductNotification(productId: String) {
        // Create intent for your MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            // Add the action to identify it's from notification
            action = Intent.ACTION_VIEW
            // Add the data as URI
            data = Uri.parse("https://yourdomain.com/product/$productId")
            // Add extra to identify source
            putExtra(EXTRA_FROM_NOTIFICATION, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Check out this product")
            .setContentText("Tap to view details")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (checkNotificationPermission()) {
            NotificationManagerCompat.from(context)
                .notify(productId.hashCode(), notification)
        }
    }


    fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    companion object {
        const val CHANNEL_ID = "product_notifications"
        private const val NOTIFICATION_ID = 1
        const val EXTRA_FROM_NOTIFICATION = "from_notification"
    }
}

