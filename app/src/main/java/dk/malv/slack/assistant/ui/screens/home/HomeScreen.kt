package dk.malv.slack.assistant.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.malv.slack.assistant.ui.Command
import dk.malv.slack.assistant.ui.components.Console
import dk.malv.slack.assistant.ui.components.CurrentStatusCard
import dk.malv.slack.assistant.ui.components.CustomStatus
import dk.malv.slack.assistant.ui.components.QuickCommands
import dk.malv.slack.assistant.utils.emoji.SlackEmoji
import dk.malv.slack.assistant.utils.emoji.emojiText
import dk.malv.slack.assistant.utils.text.colored

/**
 * Main screen of the app
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(text = "Slack Assistant")
                        Spacer(modifier = Modifier.width(16.dp))
                        // == Status ==
                        CurrentStatusCard(
                            currentStatus = state.currentStatus,
                            onReloadClick = viewModel::updateStatus,
                            modifier = Modifier
                                .weight(0.7f)
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // == Quick Commands ==
            QuickCommands(
                onClick = {
                    if (state.commandBlocked) {
                        viewModel.log("Wait... \uD83D\uDE11".colored(Color(0xFFCA6C25)))
                    } else {
                        viewModel.onQuickCommandClicked(it)
                    }
                }
            )

            // == Custom status ==
            CustomStatus(
                modifier = Modifier
                    .fillMaxWidth(),
                onApply = viewModel::onCustomStatus,
                clicksAllowed = !state.commandBlocked,
            )

            Spacer(modifier = Modifier.weight(1f))

            // == Console ==
            Console(
                logs = state.logs,
                onClear = viewModel::clearLogs,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            )
        }
    }
}

fun SlackEmoji.toCommand(
    id: String = this.code,
    title: String = this.suggestedMessage,
    emojiText: String = this.emojiText()
) = Command(id, title, emojiText)