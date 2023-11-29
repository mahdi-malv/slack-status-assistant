package dk.malv.slack.assistant.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.random.Random

@Serializable
data class UserProfile(
    val ok: Boolean,
    val profile: ProfileDetails
) {
    val statusText get() = profile.statusText
    val statusEmoji get() = profile.statusEmoji
    val statusExpiration get() = profile.statusExpiration

    companion object
}


@Serializable
data class ProfileDetails(
    val title: String,
    val phone: String,
    val skype: String,
    @SerialName("real_name")
    val realName: String,
    @SerialName("real_name_normalized")
    val realNameNormalized: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("display_name_normalized")
    val displayNameNormalized: String,
    val fields: Map<String, Field>,
    @SerialName("status_text")
    val statusText: String,
    @SerialName("status_emoji")
    val statusEmoji: String,
    @SerialName("status_emoji_display_info")
    val statusEmojiDisplayInfo: List<StatusEmojiDisplayInfo>,
    @SerialName("status_expiration")
    val statusExpiration: Long,
    @SerialName("avatar_hash")
    val avatarHash: String,
    @SerialName("start_date")
    val startDate: String,
    @SerialName("image_original")
    val imageOriginal: String,
    @SerialName("is_custom_image")
    val isCustomImage: Boolean,
    @SerialName("huddle_state")
    val huddleState: String,
    @SerialName("huddle_state_expiration_ts")
    val huddleStateExpirationTs: Long,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("image_24")
    val image24: String,
    @SerialName("image_32")
    val image32: String,
    @SerialName("image_48")
    val image48: String,
    @SerialName("image_72")
    val image72: String,
    @SerialName("image_192")
    val image192: String,
    @SerialName("image_512")
    val image512: String,
    @SerialName("image_1024")
    val image1024: String,
    @SerialName("status_text_canonical")
    val statusTextCanonical: String
) {
    companion object
}

@Serializable
data class Field(
    val value: String,
    val alt: String
) {
    companion object
}

@Serializable
data class StatusEmojiDisplayInfo(
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("display_url")
    val displayUrl: String,
    val unicode: String
) {
    companion object
}

// region Randomizers

fun UserProfile.Companion.randomize(): UserProfile {
    return UserProfile(
        ok = true,
        profile = ProfileDetails.randomize()
    )
}

fun ProfileDetails.Companion.randomize(): ProfileDetails {
    return ProfileDetails(
        title = "Software Engineer",
        phone = "+1234567890",
        skype = "skype_username",
        realName = "John Doe",
        realNameNormalized = "John Doe",
        displayName = "John",
        displayNameNormalized = "John",
        fields = mapOf("random" to Field.randomize()),
        statusText = "Available",
        statusEmoji = ":smiley:",
        statusEmojiDisplayInfo = listOf(StatusEmojiDisplayInfo.randomize()),
        statusExpiration = Random.nextLong(),
        avatarHash = "avatar_hash_value",
        startDate = "2023-09-01",
        imageOriginal = "https://example.com/image.png",
        isCustomImage = true,
        huddleState = "default_unset",
        huddleStateExpirationTs = 0,
        firstName = "John",
        lastName = "Doe",
        image24 = "https://example.com/image24.png",
        image32 = "https://example.com/image32.png",
        image48 = "https://example.com/image48.png",
        image72 = "https://example.com/image72.png",
        image192 = "https://example.com/image192.png",
        image512 = "https://example.com/image512.png",
        image1024 = "https://example.com/image1024.png",
        statusTextCanonical = ""
    )
}

fun StatusEmojiDisplayInfo.Companion.randomize(): StatusEmojiDisplayInfo {
    return StatusEmojiDisplayInfo(
        emojiName = "emoji_name",
        displayUrl = "https://example.com/display_url.png",
        unicode = "unicode_value"
    )
}

fun Field.Companion.randomize(): Field {
    return Field(
        value = "value",
        alt = "alt"
    )
}

// endregion