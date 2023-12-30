package dk.malv.slack.assistant.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

/**
 * Returns a [AnnotatedString] with the given [color] applied to the receiver [String].
 */
fun String.colored(color: Color): AnnotatedString = buildAnnotatedString {
    pushStyle(SpanStyle(color = color))
    append(this@colored)
    pop()
}

/**
 * Returns a [AnnotatedString] with the given [style] applied to the receiver [String].
 * Created to just ignore the coloring and simply make the string an annotated one
 */
fun String.annotated(style: SpanStyle = SpanStyle()): AnnotatedString =
    AnnotatedString(this, spanStyle = style)