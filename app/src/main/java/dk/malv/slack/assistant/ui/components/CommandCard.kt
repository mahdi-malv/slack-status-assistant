package dk.malv.slack.assistant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

// region Preview composable

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

// endregion
