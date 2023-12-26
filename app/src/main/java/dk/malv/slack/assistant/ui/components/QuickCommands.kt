package dk.malv.slack.assistant.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.malv.slack.assistant.ui.Command
import dk.malv.slack.assistant.ui.screens.home.toCommand
import dk.malv.slack.assistant.utils.emoji.SlackEmoji

@ExperimentalMaterial3Api
@Composable
fun QuickCommands(
    onClick: (Command) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
    ) {
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
                onClick = { onClick(it) },
                emojiCode = it.emojiText
            )
        }
    }

}