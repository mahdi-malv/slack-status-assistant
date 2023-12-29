package dk.malv.slack.assistant.ui.screens.locationbased

import android.location.Location
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.malv.slack.assistant.persistance.LocalStorage
import dk.malv.slack.assistant.persistance.getLocation
import dk.malv.slack.assistant.persistance.saveLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

private const val LOCATION_1 = "home"
private const val LOCATION_2 = "work"
private const val PERMISSION_INTRO_SHOWN = "permission_intro_shown"

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val localStorage: LocalStorage
) : ViewModel() {
    private val _state = MutableStateFlow(
        LocationScreenState(
            permissionIntroShown = localStorage.getBoolean(PERMISSION_INTRO_SHOWN),
            locations = localStorage.getLocation(LOCATION_1) to localStorage.getLocation(LOCATION_2),
            cameraState = CameraState(
                CameraProperty(
                    zoom = 15.0,
                    geoPoint = GeoPoint(55.676098, 12.568337)
                )
            )
        )
    )
    val state = _state.asStateFlow()

    // idea
    // 1. get location permission from user
    // 2. get notification permission from   user since it's needed for the service
    // 3. provide a location picker for the user that allows selecting two locations with pin (GOD HELP ME WITH THIS ONE)

    fun userNotifiedOfPermission() {
        localStorage.saveBoolean(PERMISSION_INTRO_SHOWN, true)
        _state.update { it.copy(permissionIntroShown = true) }
    }

    fun onLocationSelected(lat: Double, long: Double) {
        _state.update {
            it.copy(
                locations = it.locations.let { loc ->
                    if (loc.first == null && loc.second == null) {
                        location(lat, long) to null
                    } else if (loc.first != null && loc.second != null) {
                        location(lat, long) to loc.second
                    } else if (loc.first != null) {
                        loc.first to location(lat, long)
                    } else {
                        location(lat, long) to loc.second
                    }
                }.also { newPair ->
                    // Update the storage as well
                    localStorage.saveLocation(LOCATION_1, newPair.first)
                    localStorage.saveLocation(LOCATION_2, newPair.second)
                }
            )
        }
    }

    fun removeLocation(location: Location?) {
        _state.update {
            it.copy(
                locations = it.locations.let { loc ->
                    if (loc.first == location) {
                        null to loc.second
                    } else {
                        loc.first to null
                    }
                }.also { newPair ->
                    // Update the storage as well
                    localStorage.saveLocation(LOCATION_1, newPair.first)
                    localStorage.saveLocation(LOCATION_2, newPair.second)
                }
            )
        }
    }

    fun clearMarkers() {
        _state.update {
            it.copy(
                locations = null to null
            )
        }
        localStorage.saveLocation(LOCATION_1, null)
        localStorage.saveLocation(LOCATION_2, null)
    }

    private fun location(lat: Double, long: Double): Location {
        return Location("").apply {
            latitude = lat
            longitude = long
        }
    }
}

@Stable
data class LocationScreenState(
    val permissionIntroShown: Boolean = false,
    val locations: Pair<Location?, Location?> = null to null,
    val cameraState: CameraState = CameraState(CameraProperty())
) {
    val hasTwoLocations = locations.first != null && locations.second != null
}

