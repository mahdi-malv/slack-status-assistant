@file:Suppress("unused")
package dk.malv.slack.assistant.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Profile(
    @SerialName("status_text") val statusText: String,
    @SerialName("status_emoji") val statusEmoji: String,
    @SerialName("status_expiration") val statusExpiration: Long
)

@Serializable
class ProfileRequestBody(@SerialName("profile") val profile: Profile)