package dk.malv.slack.assistant.utils.time

import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Time(
    val time: Long = System.currentTimeMillis()
) {
    fun hourAndMin(): String {
        val date = Date(time)
        val format = SimpleDateFormat("hh:mm", Locale.getDefault())
        return format.format(date)
    }

    companion object
}

fun Time.Companion.now() = Time()