package dk.malv.slack.assistant.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrentStatusTest {
    @Test
    fun testEndTime() {
        // Create a CurrentStatus instance with a specific statusExpiration value for testing
        val currentStatus = CurrentStatus(
            statusText = "Working from home",
            statusEmoji = "🏠",
            statusExpiration = 1702508380,
            updating = false
        )
        // Call the endTime function
        val endTimeResult = currentStatus.endTime()

        // Assert the expected formatted end time
        assertEquals("2023-12-13 23:59:40", endTimeResult)
    }

}