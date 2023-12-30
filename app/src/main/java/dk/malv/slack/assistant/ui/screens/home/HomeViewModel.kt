package dk.malv.slack.assistant.ui.screens.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.malv.slack.assistant.api.client.SlackAPIClient
import dk.malv.slack.assistant.api.currentStatus
import dk.malv.slack.assistant.api.setStatus
import dk.malv.slack.assistant.receiver.BroadcastAction
import dk.malv.slack.assistant.receiver.LocalBroadcast
import dk.malv.slack.assistant.ui.Command
import dk.malv.slack.assistant.ui.components.CurrentStatus
import dk.malv.slack.assistant.ui.components.asUiStatus
import dk.malv.slack.assistant.utils.SlackEmoji
import dk.malv.slack.assistant.utils.Time
import dk.malv.slack.assistant.utils.colored
import dk.malv.slack.assistant.utils.emojiText
import dk.malv.slack.assistant.utils.now
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val slackAPIClient: SlackAPIClient,
    private val localBroadcast: LocalBroadcast
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state get() = _state.asStateFlow()

    private val wipColor = Color(0xFFDFB64E)

    init {
        // Update the status on startup
        updateStatus()
        observeLocalEvents()
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
            try {
                val status = slackAPIClient.currentStatus()
                _state.updateInUi {
                    copy(currentStatus = status.asUiStatus())
                }
            } catch (e: Exception) {
                _state.updateInUi {
                    copy(
                        currentStatus = CurrentStatus.empty(),
                        logs = logs
                            .plus(Time.now() to "~ Couldn't update status".colored(wipColor))
                            .toPersistentMap()
                    )
                }
            }
        }
    }

    /**
     * Sets the user's status on Slack with custom time, emoji, and text.
     */
    fun onCustomStatus(minutes: Int, text: String, emoji: SlackEmoji) {
        viewModelScope.launch(Dispatchers.Main) {
            slackAPIClient.setStatus(
                statusText = text.ifEmpty { emoji.suggestedMessage },
                statusEmoji = emoji.code,
                expirationTime = {
                    LocalDateTime.now()
                        .plusMinutes(minutes.toLong())
                        .atZone(ZoneId.systemDefault())
                        .toEpochSecond()
                }
            ).let {
                it.logResult(emoji.emojiText())
                updateStatus()
            }
        }
    }

    fun onQuickCommandClicked(command: Command) {
        viewModelScope.launch(Dispatchers.Main) {
            ignoreClicks()
            when (command.id) {
                "clear" -> {
                    log("~ ❌ Clearing ⌛".colored(wipColor))
                    slackAPIClient.setStatus(
                        statusText = "",
                        statusEmoji = "",
                        expirationTime = { 0 }
                    ).logResult("❌")
                }

                "commute30" -> {
                    val emoji = SlackEmoji.WALK
                    log("~ ${emoji.emojiText()} commute-30 ⌛".colored(wipColor))
                    slackAPIClient.setStatus(
                        statusText = emoji.suggestedMessage,
                        statusEmoji = emoji.code,
                        expirationTime = {
                            LocalDateTime.now()
                                .plusMinutes(30)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult(emoji.emojiText())
                }

                "nearby" -> {
                    val emoji = SlackEmoji.RUN
                    log("~ ${emoji.emojiText()} 5-min-away ⌛".colored(wipColor))
                    slackAPIClient.setStatus(
                        statusText = emoji.suggestedMessage,
                        statusEmoji = emoji.code,
                        expirationTime = {
                            LocalDateTime.now()
                                .plusMinutes(5)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult(emoji.emojiText())
                }

                "tomo" -> {
                    val emoji = SlackEmoji.HUT
                    log("~ ${emoji.emojiText()} Off-for-the-day ⌛".colored(wipColor))
                    slackAPIClient.setStatus(
                        statusText = emoji.suggestedMessage,
                        statusEmoji = emoji.code,
                        expirationTime = {
                            LocalDateTime.now()
                                .plusDays(1)
                                .withHour(7)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult(emoji.emojiText())
                }

                "next_week" -> {
                    val emoji = SlackEmoji.HUT
                    log("~ ${emoji.emojiText()} Off-for-the-week ⌛".colored(wipColor))
                    slackAPIClient.setStatus(
                        statusText = "Off for the week",
                        statusEmoji = emoji.code,
                        expirationTime = {
                            LocalDateTime
                                .now()
                                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                                .withHour(7)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult(emoji.emojiText())
                }

                "lunch" -> {
                    val emoji = SlackEmoji.LUNCH
                    log("~ ${emoji.emojiText()} Lunch-break ⌛".colored(wipColor))
                    slackAPIClient.setStatus(
                        statusText = emoji.suggestedMessage,
                        statusEmoji = emoji.code,
                        expirationTime = {
                            LocalDateTime.now()
                                .plusMinutes(45)
                                .atZone(ZoneId.systemDefault())
                                .toEpochSecond()
                        }
                    ).logResult(emoji.emojiText())
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

    fun clearLogs() {
        _state.updateInUi {
            copy(logs = persistentMapOf())
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

    private fun observeLocalEvents() {
        localBroadcast.events
            .onEach {
                _state.updateInUi {
                    copy(
                        routingInProgress =
                        it is BroadcastAction.ServiceStarted
                                || it is BroadcastAction.Destination
                                || it is BroadcastAction.DistanceUpdated
                                || it is BroadcastAction.LocationUpdated
                    )
                }
            }
            .launchIn(viewModelScope)
    }

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
    val currentStatus: CurrentStatus = CurrentStatus.empty(),
    val routingInProgress: Boolean = false
)