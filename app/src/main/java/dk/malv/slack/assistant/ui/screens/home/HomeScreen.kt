package dk.malv.slack.assistant.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import dk.malv.slack.assistant.ui.components.CommandSquare
import dk.malv.slack.assistant.ui.components.Console
import dk.malv.slack.assistant.ui.components.CurrentStatusCard
import dk.malv.slack.assistant.ui.components.CustomStatus
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
                title = { Text(text = "Slack Assistant") }
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

            // == Status ==
            CurrentStatusCard(
                currentStatus = state.currentStatus,
                onReloadClick = viewModel::updateStatus,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
            )

            // == Quick Commands ==
            LazyRow {
                items(
                    items = listOf(
                        SlackEmoji.CLEAR.toCommand(
                            id = "clear",
                            title = "Clear status",
                        ),

                        SlackEmoji.WALK.toCommand(
                            id = "commute30",
                            title = "Commuting (30)"
                        ),
                        SlackEmoji.RUN.toCommand(
                            id = "nearby",
                            title = "Nearby (5)",
                        ),
                        SlackEmoji.LUNCH.toCommand(
                            id = "lunch",
                            title = "Lunch (45)",
                        ),
                        SlackEmoji.HUT.toCommand(
                            id = "tomo",
                            title = "Off until tomorrow",
                        ),
                        SlackEmoji.HOME.toCommand(
                            id = "next_week",
                            title = "Off until next week",
                        )
                    )
                ) {
                    CommandSquare(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .fillMaxWidth(),
                        title = it.title,
                        onClick = {
                            if (state.commandBlocked) {
                                viewModel.log("Wait... \uD83D\uDE11".colored(Color(0xFFCA6C25)))
                            } else {
                                viewModel.onQuickCommandClicked(it)
                            }
                        },
                        emojiCode = it.emojiText
                    )
                }
            }

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

data class Command(
    val id: String,
    val title: String,
    val emojiText: String = ""
) {
    companion object
}

fun SlackEmoji.toCommand(
    id: String = this.code,
    title: String = this.suggestedMessage,
    emojiText: String = this.emojiText()
) = Command(id, title, emojiText)