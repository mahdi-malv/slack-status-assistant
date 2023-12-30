package dk.malv.slack.assistant.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Checks if the required permissions for accessing fine and coarse location are granted.
 * Additionally, for Android version TIRAMISU (API level 30) or higher, it also checks if the permission to post notifications is granted.
 *
 * @return true if all required permissions are granted, false otherwise
 */
fun Context.locationBasedStatusPermissionsGranted() = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
).all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }