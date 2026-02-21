package th.skylabmek.kmp_frontend.features.markdown.ui.components.editor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

interface MarkdownCommand {
    fun execute(currentValue: TextFieldValue): TextFieldValue
}

object MarkdownCommands {
    fun bold(): MarkdownCommand = WrapCommand("**")
    fun italic(): MarkdownCommand = WrapCommand("*")
    fun heading(): MarkdownCommand = LinePrefixCommand("# ")
    fun list(): MarkdownCommand = LinePrefixCommand("- ")
    fun link(): MarkdownCommand = WrapCommand("[", "](url)")
    fun image(url: String): MarkdownCommand = WrapCommand("![image]($url)", "")

    private class WrapCommand(
        private val prefix: String,
        private val suffix: String = prefix
    ) : MarkdownCommand {
        override fun execute(currentValue: TextFieldValue): TextFieldValue {
            return applyMarkdownWrapper(currentValue, prefix, suffix)
        }
    }

    private class LinePrefixCommand(
        private val prefix: String
    ) : MarkdownCommand {
        override fun execute(currentValue: TextFieldValue): TextFieldValue {
            return applyLinePrefix(currentValue, prefix)
        }
    }
}

private fun applyMarkdownWrapper(
    value: TextFieldValue,
    prefix: String,
    suffix: String = prefix
): TextFieldValue {
    val text = value.text
    val selection = value.selection

    val (start, end) = if (selection.collapsed) {
        val wordStart = (0 until selection.min).reversed()
            .firstOrNull { text[it].isWhitespace() }
            ?.plus(1) ?: 0
        val wordEnd = (selection.min..text.lastIndex)
            .firstOrNull { text[it].isWhitespace() }
            ?: text.length
        wordStart to wordEnd
    } else {
        selection.min to selection.max
    }

    val canToggle = start >= prefix.length && end + suffix.length <= text.length
    if (canToggle &&
        text.substring(start - prefix.length, start) == prefix &&
        text.substring(end, end + suffix.length) == suffix
    ) {
        val newText = text.removeRange(end, end + suffix.length)
            .removeRange(start - prefix.length, start)
        return value.copy(
            text = newText,
            selection = TextRange(start - prefix.length, end - prefix.length)
        )
    }

    val selectedText = text.substring(start, end)
    val newText = text.replaceRange(start, end, "$prefix$selectedText$suffix")
    val newSelection = if (selection.collapsed && start == end) {
        TextRange(start + prefix.length)
    } else {
        TextRange(start + prefix.length, end + prefix.length)
    }
    return value.copy(text = newText, selection = newSelection)
}

private fun applyLinePrefix(value: TextFieldValue, prefix: String): TextFieldValue {
    val text = value.text
    val lineStart = text.lastIndexOf('\n', value.selection.min - 1)
        .let { if (it == -1) 0 else it + 1 }

    return if (text.startsWith(prefix, lineStart)) {
        val newText = text.removeRange(lineStart, lineStart + prefix.length)
        value.copy(
            text = newText,
            selection = TextRange(
                (value.selection.start - prefix.length).coerceAtLeast(lineStart),
                (value.selection.end - prefix.length).coerceAtLeast(lineStart)
            )
        )
    } else {
        val newText = text.replaceRange(lineStart, lineStart, prefix)
        value.copy(
            text = newText,
            selection = TextRange(
                value.selection.start + prefix.length,
                value.selection.end + prefix.length
            )
        )
    }
}
