package dk.malv.slack.assistant.features.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.annotation.RequiresPermission

@RequiresPermission(
    allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ]
)
fun currentLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
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
