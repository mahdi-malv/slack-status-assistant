package dk.malv.slack.assistant.utils.emoji

import dk.malv.slack.assistant.R

/**
 * TODO(mahdi): Use this as the emoji component in all components
 */
enum class SlackEmoji(val code: String, val suggestedMessage: String = "") {
    HUT(":hut:", "Off for the day"),
    WALK(":walking:", "Commuting"),
    RUN(":runner:", "Nearby"),
    LEAVE(":thought_balloon:", "Unavailable"),
    HOME(":house:", "Off"),
    CLEAR(":x:", "Clear");

    // TODO(mahdi): Add more emojis such as WFH

    companion object {
        fun commuteEmojis() = listOf(
            HUT,
            WALK,
            RUN,
            LEAVE,
            HOME
        )
    }
}

fun SlackEmoji.emojiText() = when (this) {
    SlackEmoji.HUT -> "\uD83D\uDED6"
    SlackEmoji.WALK -> "\uD83D\uDEB6\u200D♂\uFE0F"
    SlackEmoji.RUN -> "\uD83C\uDFC3\u200D♂\uFE0F"
    SlackEmoji.LEAVE -> "\uD83D\uDCAD"
    SlackEmoji.HOME -> "\uD83C\uDFE0"
    SlackEmoji.CLEAR -> "❌"
}