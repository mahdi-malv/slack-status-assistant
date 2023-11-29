package dk.malv.slack.assistant.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dk.malv.slack.assistant.R
import dk.malv.slack.assistant.ui.components.CommandSquare
import dk.malv.slack.assistant.ui.components.Console
import dk.malv.slack.assistant.ui.components.CurrentStatusCard
import dk.malv.slack.assistant.utils.text.colored

/**
 * Main screen of the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val state by viewModel.state.collectAsState()

    val clearIcon = painterResource(R.drawable.ic_clear)
    val walkIcon = painterResource(R.drawable.ic_walk)
    val runIcon = painterResource(R.drawable.ic_runner)
    val leaveIcon = painterResource(R.drawable.ic_leave)
    val homeIcon = painterResource(R.drawable.ic_home)

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

            CurrentStatusCard(
                currentStatus = state.currentStatus,
                onReloadClick = viewModel::updateStatus,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(4f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = listOf(
                        Command(
                            id = "clear",
                            title = "Clear status",
                            iconPainter = clearIcon
                        ),
                        Command(
                            id = "commute30",
                            title = "Be at work in 30m",
                            iconPainter = walkIcon
                        ),
                        Command(
                            id = "nearby",
                            title = "Almost 5m away",
                            iconPainter = runIcon
                        ),
                        Command(
                            id = "tomo",
                            title = "Off until tomorrow",
                            iconPainter = leaveIcon
                        ),
                        Command(
                            id = "next_week",
                            title = "Off until next week",
                            iconPainter = homeIcon
                        ),
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
                                viewModel.onCommandClicked(it)
                            }
                        },
                        icon = it.iconPainter
                    )
                }
            }
            Console(
                logs = state.logs,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

data class Command(
    val id: String,
    val title: String,
    val iconPainter: Painter,
) {
    companion object
}