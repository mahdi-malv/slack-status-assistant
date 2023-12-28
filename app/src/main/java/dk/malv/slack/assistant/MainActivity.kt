package dk.malv.slack.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dk.malv.slack.assistant.ui.screens.home.HomeScreen
import dk.malv.slack.assistant.ui.screens.home.HomeViewModel
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
                    // Set up NavHost with Dagger support and HomeScreen as the main screen
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            // Inject HomeViewModel using Dagger
                            HomeScreen(viewModel = hiltViewModel())
                        }
                        // Add other screens to the navigation graph
                    }
                }
            }
        }
    }
}