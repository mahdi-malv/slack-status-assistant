package dk.malv.slack.assistant.ui.screens.locationbased

import android.content.Intent
import android.os.Build
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.MarkerState
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import dk.malv.slack.assistant.R
import kotlinx.collections.immutable.persistentListOf

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
                if (state.officeLocation != null) {
                    RoutingButton(
                        routingInProgress = state.routingInProgress,
                        viewModel::toggleRouting
                    )
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


    // add node
    Box(modifier = modifier) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = state.cameraState,
            properties = DefaultMapProperties.copy(
                minZoomLevel = 1.0,
                zoomButtonVisibility = ZoomButtonVisibility.SHOW_AND_FADEOUT,
            ),
            onMapLongClick = { latLng ->
                viewModel.onLocationSelected(latLng.latitude, latLng.longitude)
            }
        ) {
            state.officeLocation?.let {
                val markerState = remember(state.officeLocation) { MarkerState(state.officeGeoPoint) }
                Marker(
                    state = markerState,
                    icon = context.resources.getDrawable(R.drawable.ic_marker, null),
                    onClick = { _ -> viewModel.removeLocation(); true }
                )
            }

            if (state.routingInProgress && state.updatedLocation != null) {
                val markerState = remember(state.updatedLocation) { MarkerState(state.updatedLocationGeoPoint) }
                Marker(
                    state = markerState,
                    icon = context.resources.getDrawable(R.drawable.ic_person, null),
                )
            }
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White),
            onClick = viewModel::navigateToCurrentLocation
        ) {
            Icon(
                Icons.Filled.LocationOn,
                modifier = Modifier.size(32.dp),
                tint = Color.Black,
                contentDescription = "My Location"
            )
        }

        if (state.officeLocation != null) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (state.routingInProgress) Color.Blue else Color.White)
                    .padding(8.dp),
                text = viewModel.distanceInMeters().let {
                    if (it == 0) "Unknown distance" else "${it}m away"
                },
                fontSize = 11.sp
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RoutingButton(
    routingInProgress: Boolean,
    onClick: () -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        )

        when {
            permissionState.status.isGranted -> {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (routingInProgress)
                            Color(0xFFB46605)
                        else Color(0xFF026296)
                    )
                ) {
                    Text(text = if (routingInProgress) "Stop routing" else "Start routing")
                }
            }

            else -> {
                Button(
                    onClick = {
                        permissionState.launchPermissionRequest()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (routingInProgress)
                            Color(0xFFB46605)
                        else Color(0xFF026296)
                    )
                ) {
                    Text(text = "Routing: Allow permission")
                }
            }
        }
    } else {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (routingInProgress)
                    Color(0xFFB46605)
                else Color(0xFF026296)
            )
        ) {
            Text(text = if (routingInProgress) "Stop routing" else "Start routing")
        }
    }
}
