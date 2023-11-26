package dk.malv.slack.assistant.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.malv.slack.assistant.R

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
    subtitle: String,
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                painter = icon,
                contentDescription = title,
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * A simple preview for the [CommandCard] composable
 */
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
            subtitle = "Subtitle",
            onClick = {},
            icon = painterResource(R.drawable.ic_clear)
        )
    }
}