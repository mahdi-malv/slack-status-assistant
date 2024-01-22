package dk.malv.slack.assistant.ui.screens.locationbased.components

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.malv.slack.assistant.features.commutelog.CommuteEntry
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.Date
import java.util.Locale

@Composable
fun CommuteLogs(
    commuteTimes: ImmutableList<CommuteEntry>,
    modifier: Modifier = Modifier,
    showIfEmpty: Boolean = false,
) {
    if (commuteTimes.isEmpty() && !showIfEmpty) return
    LazyColumn(modifier = modifier) {
        // Table Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                TableCell("Start Time")
                TableCell("Arrival Time")
                TableCell("Duration")
            }
        }

        // Table Rows
        items(commuteTimes) { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                TableCell(
                    SimpleDateFormat("yy/MM/dd HH:mm", Locale.getDefault()).format(
                        Date(
                            entry.start
                        )
                    )
                )
                TableCell(
                    SimpleDateFormat("yy/MM/dd HH:mm", Locale.getDefault()).format(
                        Date(
                            entry.arrival
                        )
                    )
                )
                TableCell("${(entry.arrival - entry.start) / 60000}m")
            }
        }
    }
}

@Composable
fun RowScope.TableCell(text: String) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .border((0.2).dp, Color.Black.copy(alpha = 0.5f))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
private fun CommuteLogsPreview() {
    val commuteTimes = remember {
        persistentListOf(
            CommuteEntry(1627588800000, 1627592400000), // Example commute entry
            CommuteEntry(1627688800000, 1627692400000)  // Example commute entry
        )
    }
    CommuteLogs(commuteTimes)
}
