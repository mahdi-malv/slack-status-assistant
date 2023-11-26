package dk.malv.slack.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import dk.malv.slack.assistant.ui.screens.home.Home
import dk.malv.slack.assistant.ui.screens.home.HomeViewModel
import dk.malv.slack.assistant.ui.theme.SlackAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setContent {
            SlackAssistantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home(viewModel = viewModel)
                }
            }
        }
    }
}