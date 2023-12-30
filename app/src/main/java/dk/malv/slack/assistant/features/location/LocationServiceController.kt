package dk.malv.slack.assistant.features.location

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationServiceController @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startRoutingProcess() {
        ContextCompat.startForegroundService(context, Intent(context, RequestService::class.java))
    }

    fun stopRoutingProcess() {
        context.stopService(Intent(context, RequestService::class.java))
    }

}

