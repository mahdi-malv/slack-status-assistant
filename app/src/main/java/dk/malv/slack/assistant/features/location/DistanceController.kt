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

class DistanceController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localStorage: LocalStorage
) {
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

    fun getOfficeLocation(): Location? {
        return localStorage.getLocation(LOCATION_1)
    }

    fun clearOfficeLocation() {
        localStorage.saveLocation(LOCATION_1, null)
    }

    fun saveOfficeLocation(location: Location) {
        localStorage.saveLocation(LOCATION_1, location)
    }

    fun distanceFarFromUser(): Int {
        val current = currentLocation() ?: return 0
        val office = getOfficeLocation() ?: return 0

        return Math.round(current.distanceTo(office))
    }

    fun distanceFromOffice(location: Location): Int {
        val office = getOfficeLocation() ?: return 0
        return Math.round(location.distanceTo(office))
    }
}