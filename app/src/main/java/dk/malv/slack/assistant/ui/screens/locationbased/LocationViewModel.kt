package dk.malv.slack.assistant.ui.screens.locationbased

import android.location.Location
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.malv.slack.assistant.api.client.SlackAPIClient
import dk.malv.slack.assistant.api.setStatus
import dk.malv.slack.assistant.features.commutelog.CommuteLogPersistence
import dk.malv.slack.assistant.features.commutelog.CommuteEntry
import dk.malv.slack.assistant.features.location.DistanceController
import dk.malv.slack.assistant.features.location.LocationServiceController
import dk.malv.slack.assistant.persistance.LocalStorage
import dk.malv.slack.assistant.receiver.BroadcastAction
import dk.malv.slack.assistant.receiver.LocalBroadcast
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

private const val PERMISSION_INTRO_SHOWN = "permission_intro_shown"

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val localStorage: LocalStorage,
    private val distanceController: DistanceController,
    private val serviceController: LocationServiceController,
    private val slackAPIClient: SlackAPIClient,
    private val localBroadcast: LocalBroadcast,
    private val commuteLog: CommuteLogPersistence
) : ViewModel() {
    private val _state = MutableStateFlow(
        LocationScreenState(
            permissionIntroShown = localStorage.getBoolean(PERMISSION_INTRO_SHOWN),
            officeLocation = distanceController.getOfficeLocation(),
            cameraState = CameraState(
                CameraProperty(
                    zoom = 15.0,
                    geoPoint = GeoPoint(55.676098, 12.568337)
                )
            )
        )
    )
    val state = _state.asStateFlow()

    init {
        observeLocalEvents()
        observeCommuteTimes()
    }

    fun userNotifiedOfPermission() {
        localStorage.saveBoolean(PERMISSION_INTRO_SHOWN, true)
        _state.update { it.copy(permissionIntroShown = true) }
    }

    fun navigateToCurrentLocation() {
        val current = distanceController.currentLocation()
        if (current != null) {
            _state.update {
                it.copy(
                    cameraState = CameraState(
                        CameraProperty(
                            zoom = 15.0,
                            geoPoint = GeoPoint(current.latitude, current.longitude)
                        )
                    )
                )
            }
        }
    }

    fun onLocationSelected(lat: Double, long: Double) = _state.update {
        val loc = location(lat, long)
        distanceController.saveOfficeLocation(loc)
        it.copy(officeLocation = loc)
    }

    fun removeLocation() = _state.update {
        distanceController.clearOfficeLocation()
        it.copy(officeLocation = null)
    }

    fun toggleRouting() {
        if (_state.value.routingInProgress) {
            serviceController.stopRoutingProcess()
            clearSlackStatus()
            _state.update { it.copy(routingInProgress = false) }
        } else {
            serviceController.startRoutingProcess()
            _state.update { it.copy(routingInProgress = true) }
        }
    }

    fun distanceInMeters(): Int {
        return distanceController.distanceFarFromUser()
    }

    private fun location(lat: Double, long: Double): Location {
        return Location("").apply {
            latitude = lat
            longitude = long
        }
    }

    private fun observeLocalEvents() {
        localBroadcast.events
            .onEach { action ->
                when (action) {
                    is BroadcastAction.Destination, is BroadcastAction.ServiceStopped -> {
                        _state.update { it.copy(routingInProgress = false) }
                    }

                    is BroadcastAction.DistanceUpdated -> {
                        _state.update { it.copy(updatedDistance = action.distance) }
                    }

                    is BroadcastAction.Idle -> {
                    }

                    is BroadcastAction.LocationUpdated -> {
                        _state.update { it.copy(updatedLocation = action.location) }
                    }

                    is BroadcastAction.ServiceStarted -> {
                        _state.update { it.copy(routingInProgress = true) }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun observeCommuteTimes() {
        commuteLog.commuteTimes
            .onEach { times ->
                _state.update { it.copy(commuteEntries = times.toImmutableList()) }
            }.launchIn(viewModelScope)
    }

    private fun clearSlackStatus() {
        viewModelScope.launch { slackAPIClient.setStatus("", "", false) { 0 } }
    }
}

@Stable
data class LocationScreenState(
    val permissionIntroShown: Boolean = false,
    val officeLocation: Location? = null,
    val cameraState: CameraState = CameraState(CameraProperty()),
    val routingInProgress: Boolean = false,
    val updatedLocation: Location? = null,
    val updatedDistance: Int = 0,
    val commuteEntries: ImmutableList<CommuteEntry> = persistentListOf()
) {
    val officeGeoPoint: GeoPoint = officeLocation?.let {
        GeoPoint(it.latitude, it.longitude)
    } ?: GeoPoint(0.0, 0.0)

    val updatedLocationGeoPoint: GeoPoint = updatedLocation?.let {
        GeoPoint(it.latitude, it.longitude)
    } ?: GeoPoint(0.0, 0.0)
}

