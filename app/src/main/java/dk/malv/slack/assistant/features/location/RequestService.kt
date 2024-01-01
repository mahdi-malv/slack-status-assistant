package dk.malv.slack.assistant.features.location

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import dk.malv.slack.assistant.R
import dk.malv.slack.assistant.api.client.SlackAPIClient
import dk.malv.slack.assistant.api.setStatus
import dk.malv.slack.assistant.receiver.BroadcastAction
import dk.malv.slack.assistant.receiver.LocalBroadcast
import dk.malv.slack.assistant.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

private const val NOTIFICATION_ID = 0x123485
private const val NOTIFICATION_CHANNEL_ID = "location_tracking_channel"
private const val REQUEST_INTERVAL = 30_000L

@AndroidEntryPoint
class RequestService : Service() {

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var slackAPIClient: SlackAPIClient

    @Inject
    lateinit var distanceController: DistanceController

    @Inject
    lateinit var localBroadcast: LocalBroadcast

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val notificationManager get() = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("Starting location service")
        if (distanceController.getOfficeLocation() == null) {
            log("No office location set, stopping service")
            localBroadcast.send(BroadcastAction.ServiceStopped)
            stopSelf()
            return START_NOT_STICKY
        }

        createChannel()

        localBroadcast.send(BroadcastAction.ServiceStarted)
        startForeground(
            NOTIFICATION_ID,
            notifyOf("Location Tracking Service", "Tracking your location")
        )
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log("Creating location service")
        periodicallyRequestForLocation()
    }

    /**
     * Uses FusedLocationProviderClient to periodically request for location updates.
     * gets the location every 15 second
     */
    @SuppressLint("MissingPermission")
    private fun periodicallyRequestForLocation() {
        val request = LocationRequest.Builder(
            /* priority = */ Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            /* intervalMillis = */ REQUEST_INTERVAL,
        ).build()


        fusedLocationClient.requestLocationUpdates(request, ::locationUpdated, null)
    }

    private fun locationUpdated(location: Location) {
        localBroadcast.send(BroadcastAction.LocationUpdated(location))
        serviceScope.launch {
            // Update the status
            val distance = distanceController.distanceFromOffice(location)
            localBroadcast.send(BroadcastAction.DistanceUpdated(distance))
            // If we are less than 100m away, stop the service and clear the status
            if (distance < 100) {
                slackAPIClient.setStatus(
                    statusText = "",
                    statusEmoji = "",
                    dryRun = false,
                    expirationTime = { 0 }
                )
                localBroadcast.send(BroadcastAction.Destination)
                stopSelf()
                return@launch
            }

            slackAPIClient.setStatus(
                statusText = "Commuting, ${distance}m away",
                statusEmoji = ":walking:",
                dryRun = false,
            ) {
                LocalDateTime.now()
                    .plusMinutes(5) // TODO(mahdi): What to do for the expiration time?
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond()
            }
            notificationManager.notify(
                NOTIFICATION_ID,
                notifyOf(
                    "You are commuting to the office \uD83D\uDEB6\u200D♂\uFE0F",
                    "${distance}m away"
                )
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(::locationUpdated)
        serviceScope.cancel()
    }

    private fun createChannel() {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Tracking Service",
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    private fun notifyOf(
        title: String,
        content: String
    ) = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.ic_person)
        .setOngoing(true)
        .build()

}