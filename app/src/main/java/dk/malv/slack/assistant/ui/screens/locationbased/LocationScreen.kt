package dk.malv.slack.assistant.ui.screens.locationbased

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polygon
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import dk.malv.slack.assistant.R
import dk.malv.slack.assistant.features.location.calculateDistanceInMeters
import dk.malv.slack.assistant.features.location.currentLocation
import dk.malv.slack.assistant.utils.log
import kotlinx.collections.immutable.persistentListOf
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val permissionState = rememberMultiplePermissionsState(
        permissions = persistentListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    ) { viewModel.userNotifiedOfPermission() }

    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    when {
        permissionState.allPermissionsGranted -> {
            // Render the UI
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(text = "Select home and office location", modifier = Modifier.padding(8.dp))
                Spacer(Modifier.height(16.dp))
                MapViewScreen(
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(Modifier.height(16.dp))
                if (state.hasTwoLocations) {
                    Button(onClick = {}) {
                        Text(text = "Start routing")
                    }
                }
                Spacer(Modifier.weight(0.5f))
            }
        }

        permissionState.shouldShowRationale || !state.permissionIntroShown -> {
            // Show a dialog explaining why we need the permissions
            PermissionNeededUi(
                modifier = Modifier.padding(8.dp),
                shouldShowRationale = true,
                onAllowClicked = permissionState::launchMultiplePermissionRequest
            )
        }

        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
            // Show a Ui explaining why the permission is needed
            PermissionNeededUi(
                modifier = Modifier.padding(8.dp),
                shouldShowRationale = false,
                onAllowClicked = {
                    activityResultLauncher.launch(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    })
                }
            )
        }
    }
}

@Composable
private fun PermissionNeededUi(
    shouldShowRationale: Boolean,
    onAllowClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val text = if (shouldShowRationale) {
        "If you want this service, you must let the app use location services"
    } else {
        "Couldn't show the dialog. For location based status, app needs the permission"
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(text, modifier = Modifier.padding(8.dp))
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onAllowClicked) {
            Text(text = "Allow")
        }
    }
}

@Composable
private fun MapViewScreen(
    viewModel: LocationViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(55.663563, 12.591235)
        zoom = 14.0 // optional, default is 5.0
    }


    // add node
    Box(modifier = modifier) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            properties = DefaultMapProperties.copy(
                minZoomLevel = 1.0,
                zoomButtonVisibility = ZoomButtonVisibility.SHOW_AND_FADEOUT,
            ),
            onMapLongClick = { latLng ->
                viewModel.onLocationSelected(latLng.latitude, latLng.longitude)
            }
        ) {
            state.locations.let {
                if (it.first != null) {
                    log("Marker 1 - ${it.first?.run { latitude to longitude }}")
                    Marker(
                        state = rememberMarkerState(
                            geoPoint = GeoPoint(it.first!!.latitude, it.first!!.longitude)
                        ),
                        icon = context.resources.getDrawable(R.drawable.ic_marker, null),
                        onClick = { _ -> viewModel.removeLocation(it.first); true }
                    )
                }
                if (it.second != null) {
                    log("Marker 2 - ${it.second?.run { latitude to longitude }}")
                    Marker(
                        state = rememberMarkerState(
                            geoPoint = GeoPoint(it.second!!.latitude, it.second!!.longitude)
                        ),
                        icon = context.resources.getDrawable(R.drawable.ic_marker, null),
                        onClick = { _ -> viewModel.removeLocation(it.second); true }
                    )
                }
                if (state.hasTwoLocations) {
                    val points = remember(it) {
                        persistentListOf(
                            GeoPoint(it.first!!.latitude, it.first!!.longitude),
                            GeoPoint(it.second!!.latitude, it.second!!.longitude)
                        )
                    }
                    log(
                        "Distance: ${
                            calculateDistanceInMeters(
                                it.first!!,
                                it.second!!
                            )
                        }m - ${it.first?.run { latitude to longitude }} - ${it.second?.run { latitude to longitude }} }"
                    )
                    Polygon(
                        outlineColor = Color(0xFF5ABB99),
                        geoPoints = points,
                        title = "Distance: ${calculateDistanceInMeters(it.first!!, it.second!!)}m"
                    )
                }
            }
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White),
            onClick = {
                currentLocation(context)?.let {
                    cameraState.geoPoint = GeoPoint(it.latitude, it.longitude)
                    cameraState.zoom = 16.0
                }
            }) {
            Icon(
                Icons.Filled.LocationOn,
                modifier = Modifier.size(32.dp),
                tint = Color.Black,
                contentDescription = "My Location"
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopStart),
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White),
            onClick = viewModel::clearMarkers) {
            Icon(
                Icons.Filled.Clear,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFFFF6A2E),
                contentDescription = "Clear"
            )
        }

        if (state.hasTwoLocations) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White)
                    .padding(2.dp),
                text = "${calculateDistanceInMeters(state.locations.first!!, state.locations.second!!)}m",
            )
        }
    }
}
