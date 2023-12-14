package dk.malv.slack.assistant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A composable function that simply acts as a card item
 * It represents a card that gets pressed and does something
 *
 * A square card with a title and a subtitle and an image or icon
 * @param title is the title of the card
 * @param subtitle is the subtitle of the card that explains what it does
 * @param onClick is the action that happens when the card is pressed. just a normal no parameter callback
 * @param modifier is the modifier for the card
 */
@ExperimentalMaterial3Api
@Composable
fun CommandCard(
    title: String,
    emojiCode: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        onClick = { onClick?.invoke() },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                text = emojiCode,
                style = TextStyle(textAlign = TextAlign.Center, baselineShift = BaselineShift.Subscript),
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CommandSquare(
    title: String,
    emojiCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val squareWidth = (LocalConfiguration.current.screenWidthDp / 5).dp

    Card(
        onClick = onClick,
        modifier = modifier
            .size(squareWidth)
            .padding(2.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 0.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emojiCode,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp
                ),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CommandPreview() {
    Surface(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxWidth()
    ) {
        CommandCard(
            title = "Title",
            onClick = {},
            emojiCode = "❌"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
@Suppress("UnusedPrivateMember")
private fun CommandSquarePreview() {
    CommandSquare(
        title = "Sample Title",
        emojiCode = "🚶‍♂️",
        modifier = Modifier,
        onClick = {}
    )
}
