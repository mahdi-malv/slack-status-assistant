package dk.malv.slack.assistant.features.commutelog

import dk.malv.slack.assistant.persistance.LocalStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val KEY_COMMUTE_LOG = "commute_log"

class CommuteLogPersistence @Inject constructor(
    private val localStorage: LocalStorage
) {

    /**
     * A stateFlow of the commute times
     */
    val commuteTimes = MutableStateFlow<List<CommuteEntry>>(emptyList())

    /**
     * Saves a commute time to SharedPreferences.
     * @param start The time of departure
     * @param arrival The time of arrival
     *
     * Uses [CommuteEntry] to store the commute time.
     */
    fun saveCommute(start: Long, arrival: Long) {
        val commute = CommuteEntry(start, arrival)
        val json = Json.encodeToString(commute)
        localStorage.modifyStringListValue(KEY_COMMUTE_LOG, json)
        commuteTimes.tryEmit(getCommuteTimes())
    }

    private fun getCommuteTimes(): List<CommuteEntry> {
        return localStorage.getStringList(KEY_COMMUTE_LOG)
            .map { Json.decodeFromString(it) }
    }

}

data class CommuteEntry(
    val start: Long,
    val arrival: Long
)