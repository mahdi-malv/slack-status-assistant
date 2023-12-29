package dk.malv.slack.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dk.malv.slack.assistant.ui.components.CurrentStatus
import dk.malv.slack.assistant.ui.components.CurrentStatusCard
import dk.malv.slack.assistant.ui.screens.home.HomeScreen
import dk.malv.slack.assistant.ui.screens.home.HomeViewModel
import dk.malv.slack.assistant.ui.screens.locationbased.LocationScreen
import dk.malv.slack.assistant.ui.theme.SlackAssistantTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Dagger component

        setContent {

            val navController = rememberNavController()

            SlackAssistantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    // Set up NavHost with Dagger support and HomeScreen as the main screen
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            MainTopBar(
                                currentStatus = homeViewModel.state.collectAsState().value.currentStatus, // BAD idea I think
                                onReloadClick = homeViewModel::updateStatus
                            )
                        }
                    ) {
                        NavHost(
                            modifier = Modifier.padding(it),
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") {
                                // Inject HomeViewModel using Dagger
                                HomeScreen(viewModel = homeViewModel, navController = navController)
                            }
                            composable("location") {
                                LocationScreen(viewModel = hiltViewModel())
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainTopBar(
        currentStatus: CurrentStatus,
        onReloadClick: () -> Unit
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(text = "Slack Assistant")
                    Spacer(modifier = Modifier.width(16.dp))
                    // == Status ==
                    CurrentStatusCard(
                        currentStatus = currentStatus,
                        onReloadClick = onReloadClick,
                        modifier = Modifier
                            .weight(0.7f)
                    )
                }
            }
        )
    }
}