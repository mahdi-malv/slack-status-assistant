package dk.malv.slack.assistant.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.malv.slack.assistant.utils.emoji.SlackEmoji
import dk.malv.slack.assistant.utils.emoji.emojiText

/**
 * Composable function to display a custom status UI with emoji icons, text field, seekbar, and apply button.
 *
 * Usage:
 *
 * ```kt
 * CustomStatus(
 *     modifier = Modifier,
 *     onApply = { minutes, text, emoji ->
 *         // Handle the apply button click event
 *     }
 * )
 *```
 *
 * @param modifier The modifier for the layout
 * @param onApply Callback function when the apply button is clicked, providing minutes, text, and selected emoji
 */
@ExperimentalComposeUiApi
@Composable
fun CustomStatus(
    modifier: Modifier = Modifier,
    onApply: (minutes: Int, text: String, emoji: SlackEmoji) -> Unit
) = Card(
    modifier = modifier.padding(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(
        containerColor = Color.Gray.copy(alpha = 0.5f),
    )
) {
    val (sliderPosition, updateSliderPosition) = remember { mutableFloatStateOf(0f) }
    val (textFieldText, updateText) = remember { mutableStateOf("ETA") }

    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        // Horizontal list of emoji icons
        var selectedEmoji by remember { mutableStateOf<SlackEmoji?>(null) }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(selectedEmoji) {
            // Update the text due to emoji selection
            updateText(selectedEmoji?.suggestedMessage ?: "ETA")
        }

        // Emojis select row
        Row {
            for (emoji in SlackEmoji.commuteEmojis()) {
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            selectedEmoji = if (selectedEmoji == emoji) {
                                null
                            } else {
                                emoji
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedEmoji == emoji) Color.White else Color.Transparent,
                    ),
                ) {
                    Text(
                        text = emoji.emojiText(),
                        style = TextStyle(textAlign = TextAlign.Center, fontSize = 24.sp),
                        modifier = Modifier
                            .padding(4.dp)
                            .size(32.dp)
                    )
                }
            }
        }


        // Text field with default text "ETA"
        TextField(
            value = textFieldText,
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            ),
            onValueChange = {
                if (it.length < 50) updateText(it)
            },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        // Seekbar allowing from 1 to 100
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sliderPosition.toInt().toString(),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    updateSliderPosition(newValue)
                },
                valueRange = 0f..100f
            )
        }

        // Summary text
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Set \"$textFieldText\" for \"${sliderPosition.toInt()}\"m (${selectedEmoji?.run { "$code ${emojiText()}" } ?: "Default Emoji"})",
            style = TextStyle(fontSize = 14.sp, textAlign = TextAlign.Center)
        )

        // Apply button
        Button(
            enabled = sliderPosition.toInt() > 0,
            onClick = {
                onApply(sliderPosition.toInt(), textFieldText, selectedEmoji ?: SlackEmoji.LEAVE)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun CustomStatusPreview() {
    CustomStatus(
        modifier = Modifier,
        onApply = { minutes, text, emoji ->
            // Handle the apply button click event for preview
        }
    )
}

@Preview
@Composable
fun SliderWithNumberPreview() {
    val (sliderPosition, updateSliderPosition) = remember { mutableFloatStateOf(0f) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sliderPosition.toInt().toString(),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
            value = sliderPosition,
            onValueChange = updateSliderPosition,
            valueRange = 0f..100f
        )
    }
}

