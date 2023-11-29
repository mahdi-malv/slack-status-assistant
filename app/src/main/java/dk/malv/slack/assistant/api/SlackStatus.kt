package dk.malv.slack.assistant.api

import dk.malv.slack.assistant.api.model.Profile
import dk.malv.slack.assistant.api.model.ProfileRequestBody
import dk.malv.slack.assistant.api.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Sets the user's status on Slack.
 *
 * @param statusText The text representing the user's status. Default value is "Off for the day".
 * @param statusEmoji The emoji representing the user's status. Default value is ":hut:".
 * @param expirationTime A function that provides the expiration time of the user's status.
 *                      It defaults to 7:00 AM of the next day.
 * @return A boolean indicating the success of the operation.
 */
suspend fun Client.setStatus(
    statusText: String,
    statusEmoji: String,
    expirationTime: () -> Long
): Boolean = withContext(Dispatchers.IO) {
    val profile = Profile(
        statusText,
        statusEmoji,
        expirationTime()
    )
    val json: String = post(
        url = "https://slack.com/api/users.profile.set",
        body = ProfileRequestBody(profile)
    )
    "\"ok\":true" in json // Definitely needs a better parsing
}

/**
 * Gets the user's status on Slack.
 *
 * @return A UserProfile representing the user's status.
 */
suspend fun Client.currentStatus(): UserProfile = withContext(Dispatchers.IO) {
    // Make a GET request to retrieve the user's profile from Slack API
    get("https://slack.com/api/users.profile.get")
}

