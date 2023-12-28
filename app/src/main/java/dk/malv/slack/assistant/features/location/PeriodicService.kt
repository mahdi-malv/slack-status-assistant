package dk.malv.slack.assistant.features.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Timer
import java.util.TimerTask

private const val DEFAULT_INTERVAL: Long = 20000 // 30 seconds in milliseconds
private const val NOTIFICATION_ID = 0x123485
private const val NOTIFICATION_CHANNEL_ID = "location_tracking_channel"

class LocationTrackingService : Service() {
    private val timer = Timer()
    private val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationChannel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        "Location Tracking Service",
        NotificationManager.IMPORTANCE_DEFAULT
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return START_STICKY
    }

    /**
     * Needs Location permission + notification permission
     */
    private fun startForegroundService() {
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = notifyOf(
            title = "Location Tracking Service",
            content = "Tracking your location"
        )
        startForeground(NOTIFICATION_ID, notification)

        if (locationBasedStatusPermissionsGranted()) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                @SuppressLint("MissingPermission") // Already checked
                override fun run() {
                    // Step 1 get the location
                    val location: Location? = currentLocation(this@LocationTrackingService)

                    // Step 2 get the distance as text
                    // ...

                    // Step 3 if not too close, update
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        notifyOf("Location based status", "800m away")
                    )

                    // Step 4 loop if not canceled
                }
            }, 0, DEFAULT_INTERVAL)
        } else {
            stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    private fun notifyOf(
        title: String,
        content: String
    ) = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(android.R.drawable.ic_dialog_map)
        .build()
}

/**
 * Checks if the required permissions for accessing fine and coarse location are granted.
 * Additionally, for Android version TIRAMISU (API level 30) or higher, it also checks if the permission to post notifications is granted.
 *
 * @return true if all required permissions are granted, false otherwise
 */
fun Context.locationBasedStatusPermissionsGranted() = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
).all { ContextCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED } &&
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PERMISSION_GRANTED
        } else {
            true
        }