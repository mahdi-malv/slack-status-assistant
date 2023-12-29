package dk.malv.slack.assistant.features.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import dk.malv.slack.assistant.utils.locationBasedStatusPermissionsGranted

@SuppressLint("MissingPermission") // Already checked
fun currentLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (context.locationBasedStatusPermissionsGranted())
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    else null
}


/**
 * Calculates the distance in meters between two locations.
 *
 * @param location1 the first location
 * @param location2 the second location
 * @return the distance in meters as an integer (rounded)
 */
fun calculateDistanceInMeters(location1: Location, location2: Location): Int {
    return Math.round(location1.distanceTo(location2))
}
