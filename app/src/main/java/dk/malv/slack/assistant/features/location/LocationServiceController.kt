package dk.malv.slack.assistant.features.location

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Controller class for managing the location service.
 *
 * @param context the application context
 */
class LocationServiceController @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Starts the routing process by starting the location service in the foreground.
     */
    fun startRoutingProcess() {
        ContextCompat.startForegroundService(context, Intent(context, RequestService::class.java))
    }

    /**
     * Stops the routing process by stopping the location service.
     */
    fun stopRoutingProcess() {
        context.stopService(Intent(context, RequestService::class.java))
    }
}


