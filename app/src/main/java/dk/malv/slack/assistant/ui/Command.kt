package dk.malv.slack.assistant.ui

/**
 * A command that can be executed
 * @param id unique id of the command
 * @param title title of the command
 * @param emojiText emoji text of the command
 * @see dk.malv.slack.assistant.utils.emoji.emojiText
 */
data class Command(
    val id: String,
    val title: String,
    val emojiText: String = ""
) {
    companion object
}