package dk.malv.slack.assistant.features.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.malv.slack.assistant.persistance.LocalStorage
import dk.malv.slack.assistant.persistance.getLocation
import dk.malv.slack.assistant.persistance.saveLocation
import dk.malv.slack.assistant.utils.locationBasedStatusPermissionsGranted
import javax.inject.Inject

private const val LOCATION_1 = "work_location"

/**
 * Controller class for managing distance-related operations and location data.
 *
 * @param context the application context
 * @param localStorage the local storage instance for saving and retrieving location data
 */
class DistanceController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localStorage: LocalStorage
) {
    /**
     * Retrieves the current device location.
     *
     * @return the current location if available, or null
     */
    @SuppressLint("MissingPermission")
    fun currentLocation(): Location? {
        return try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (context.locationBasedStatusPermissionsGranted())
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Retrieves the office location from local storage.
     *
     * @return the office location if available, or null
     */
    fun getOfficeLocation(): Location? {
        return localStorage.getLocation(LOCATION_1)
    }

    /**
     * Clears the saved office location from local storage.
     */
    fun clearOfficeLocation() {
        localStorage.saveLocation(LOCATION_1, null)
    }

    /**
     * Saves the provided location as the office location in local storage.
     *
     * @param location the office location to be saved
     */
    fun saveOfficeLocation(location: Location) {
        localStorage.saveLocation(LOCATION_1, location)
    }

    /**
     * Calculates the distance in meters between the user's current location and the office location.
     *
     * @return the distance in meters as an integer (rounded)
     */
    fun distanceFarFromUser(): Int {
        val current = currentLocation() ?: return 0
        val office = getOfficeLocation() ?: return 0

        return Math.round(current.distanceTo(office))
    }

    /**
     * Calculates the distance in meters between the provided location and the office location.
     *
     * @param location the target location
     * @return the distance in meters as an integer (rounded)
     */
    fun distanceFromOffice(location: Location): Int {
        val office = getOfficeLocation() ?: return 0
        return Math.round(location.distanceTo(office))
    }
}
