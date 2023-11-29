package dk.malv.slack.assistant.ui.screens.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.malv.slack.assistant.api.Client
import dk.malv.slack.assistant.api.currentStatus
import dk.malv.slack.assistant.ui.components.CurrentStatus
import dk.malv.slack.assistant.ui.components.currentStatus
import dk.malv.slack.assistant.api.setStatus
import dk.malv.slack.assistant.utils.text.colored
import dk.malv.slack.assistant.utils.time.Time
import dk.malv.slack.assistant.utils.time.now
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters


class HomeViewModel(
    private val client: Client = Client() // Needs DI 😂
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state get() = _state.asStateFlow()

    private val wipColor = Color(0xFFDFB64E)

    init {
        // Update the status on startup
        updateStatus()
    }

    /**
     * Updates the current status of the user
     */
    fun updateStatus() {
        viewModelScope.launch(Dispatchers.Main) {
            _state.updateInUi {
                copy(
                    currentStatus = currentStatus.copy(updating = true),
                    logs = logs
                        .plus(Time.now() to "~ Updating status...".colored(Color.Gray))
                        .toPersistentMap()
                )
            }
            val status = client.currentStatus()
            _state.updateInUi {
                copy(currentStatus = status.currentStatus())
            }
        }
    }

    fun onCommandClicked(command: Command) {
        viewModelScope.launch(Dispatchers.Main) {
            ignoreClicks()
            when (command.id) {
                "clear" -> {
                    log("~ ❌ Clearing ⌛".colored(wipColor))
                    client.setStatus(
                        statusText = "",
                        statusEmoji = "",
                        expirationTime = { 0 }
                    ).logResult("❌")
                }
                "commute30" -> {
                    log("~ \uD83D\uDEB6\u200D♂\uFE0F 30-min-away ⌛".colored(wipColor))
                    client.setStatus(
                        statusText = "Will be at the office in 30 minutes",
                        statusEmoji = ":walking:",
                        expirationTime = {
                            LocalDateTime.now()
                                .plusMinutes(30)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult("\uD83D\uDEB6\u200D♂\uFE0F")
                }

                "nearby" -> {
                    log("~ \uD83D\uDEB6\u200D♂\uFE0F 5-min-away ⌛".colored(wipColor))
                    client.setStatus(
                        statusText = "Will be at the office in 5 minutes",
                        statusEmoji = ":walking:",
                        expirationTime = {
                            LocalDateTime.now()
                                .plusMinutes(5)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult("\uD83D\uDEB6\u200D♂\uFE0F")
                }

                "tomo" -> {
                    log("~ \uD83D\uDED6 Off-for-the-day ⌛".colored(wipColor))
                    client.setStatus(
                        statusText = "Off for the day",
                        statusEmoji = ":hut:",
                        expirationTime = {
                            LocalDateTime.now()
                                .plusDays(1)
                                .withHour(7)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult("\uD83D\uDED6")
                }

                "next_week" -> {
                    log("~ \uD83D\uDED6 Off-for-the-week ⌛".colored(wipColor))
                    client.setStatus(
                        statusText = "Off for the week",
                        statusEmoji = ":hut:",
                        expirationTime = {
                            LocalDateTime
                                .now()
                                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                                .withHour(7)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult("\uD83D\uDED6")
                }

                else -> {
                    log("Unknown command".colored(Color.Red.copy(alpha = 0.7f)))
                    delay(20)
                    allowClicks()
                    return@launch
                }
            }

            allowClicks()
            updateStatus()
        }
    }


    // region Ui state controllers

    private fun Boolean.logResult(emojiText: String) {
        if (this)
            log("$emojiText Status set ✓".colored(Color.Green.copy(alpha = 0.5f)))
        else
            log("Couldn't set the status".colored(Color.Red))
    }

    fun log(text: AnnotatedString, time: Time = Time.now()) = _state.updateInUi {
        copy(
            logs = (logs.plus(time to text)).toPersistentMap()
        )
    }

    private fun ignoreClicks() = _state.updateInUi {
        copy(commandBlocked = true)
    }

    private fun allowClicks() = _state.updateInUi {
        copy(commandBlocked = false)
    }

    // endregion

    private fun MutableStateFlow<HomeUiState>.updateInUi(block: suspend HomeUiState.() -> HomeUiState) {
        viewModelScope.launch {
            value = value.block()
        }
    }
}

data class HomeUiState(
    /**
     * To avoid fucking up the onClick
     */
    val commandBlocked: Boolean = false,
    val logs: ImmutableMap<Time, AnnotatedString> = persistentMapOf(),
    val currentStatus: CurrentStatus = CurrentStatus.empty()
)