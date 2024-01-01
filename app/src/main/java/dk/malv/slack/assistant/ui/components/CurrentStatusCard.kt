package dk.malv.slack.assistant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.malv.slack.assistant.R
import dk.malv.slack.assistant.api.model.UserProfile
import dk.malv.slack.assistant.utils.SlackEmoji
import dk.malv.slack.assistant.utils.emojiText
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class CurrentStatus(
    val statusText: String,
    val statusEmoji: String,
    val statusExpiration: Long,
    val updating: Boolean
) {
    val isEmpty get() = statusText.isEmpty() && statusEmoji.isEmpty()

    fun endTime(): String {
        val instant = Instant.ofEpochMilli(statusExpiration * 1000) // Convert to milliseconds
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }


    companion object {
        fun empty() = CurrentStatus(
            statusText = "",
            statusEmoji = "",
            statusExpiration = 0,
            updating = false
        )
    }
}

fun UserProfile.asUiStatus(): CurrentStatus = CurrentStatus(
    statusText = statusText,
    statusEmoji = statusEmoji,
    statusExpiration = statusExpiration,
    updating = false
)

/**
 * Composable function to display the current status card.
 *
 * @param currentStatus The current status of the user.
 * @param modifier Optional modifier for the card.
 */
@ExperimentalMaterial3Api
@Composable
fun CurrentStatusCard(
    currentStatus: CurrentStatus,
    modifier: Modifier = Modifier,
    onReloadClick: () -> Unit = {},
) {
    if (currentStatus.updating) {
        Card(
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFFD5A11E)
                )
            }
        }
        return
    }

    if (currentStatus.isEmpty) {
        Card(
            modifier = modifier,
            onClick = onReloadClick
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "No status set",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF6D6A61)
                )
            }
        }
        return
    }

    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = SlackEmoji.entries.find { it.code == currentStatus.statusEmoji }?.emojiText()
                    ?: currentStatus.statusEmoji,
                modifier = Modifier.size(24.dp)
            )


            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentStatus.statusText,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(fontSize = 12.sp)
                )
                Text(
                    text = currentStatus.endTime(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = TextStyle(fontSize = 8.sp)
                )
            }

            IconButton(
                onClick = onReloadClick,
            ) {
                Icon(
                    modifier = Modifier.padding(12.dp),
                    painter = painterResource(R.drawable.ic_reload),
                    tint = Color(0xFFDFB64E),
                    contentDescription = "Reload status",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CurrentStatusCardPreview() {
    val status = remember {
        CurrentStatus(
            "Walking home",
            ":thought_balloon:",
            statusExpiration = 1629950400,
            updating = false
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrentStatusCard(status, modifier = Modifier.size(200.dp, 40.dp))
        Spacer(Modifier.height(12.dp))
        CurrentStatusCard(CurrentStatus.empty(), modifier = Modifier.size(200.dp, 40.dp))
        Spacer(Modifier.height(12.dp))
        CurrentStatusCard(
            CurrentStatus.empty().copy(updating = true),
            modifier = Modifier.size(200.dp, 40.dp)
        )
    }
}
