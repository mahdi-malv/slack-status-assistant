package dk.malv.slack.assistant.receiver

import android.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalBroadcast @Inject constructor() {

    private val _state: MutableStateFlow<BroadcastAction> = MutableStateFlow(BroadcastAction.Idle)
    val events = _state.asStateFlow()

    fun send(action: BroadcastAction) {
        _state.update { action }
    }
}

sealed interface BroadcastAction {
    data object Idle : BroadcastAction
    data class LocationUpdated(val location: Location) : BroadcastAction
    data class DistanceUpdated(val distance: Int) : BroadcastAction
    data object ServiceStarted : BroadcastAction
    data object ServiceStopped : BroadcastAction
    data object Destination : BroadcastAction
}