package dk.malv.slack.assistant.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.malv.slack.assistant.utils.annotated
import dk.malv.slack.assistant.utils.Time
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Composable
fun Console(
    logs: ImmutableMap<Time, AnnotatedString>,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val consoleColor = remember { Color(0xFF5AA7E0) }
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        val listState = rememberLazyListState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                reverseLayout = true,
            ) {
                items(
                    items = logs.toList(),
                    key = { (time, _) -> time.time }
                ) { log ->
                    Divider(color = consoleColor.copy(alpha = 0.4f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = log.second,
                            style = TextStyle(color = consoleColor, fontSize = 16.sp)
                        )
                        Text(
                            text = log.first.hourAndMin(),
                            style = TextStyle(
                                color = consoleColor.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
            IconButton(
                onClick = onClear,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Gray.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.Clear, contentDescription = "Clear console", tint = Color.Red)
            }
        }
        // If item size changed, scroll to the most recent item
        LaunchedEffect(logs.size) {
            if (logs.isEmpty()) return@LaunchedEffect
            listState.animateScrollToItem(logs.size - 1)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConsolePreview() {
    Console(
        onClear = {},
        logs = persistentMapOf(
            Time(1L) to "\$ Command X \uD83D\uDD66".annotated(SpanStyle(color = Color.Red)),
            Time(2L) to "\$ Command Y \uD83D\uDD66".annotated(),
        )
    )
}
