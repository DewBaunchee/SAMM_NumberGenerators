package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.symbol

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.TextPart
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import javafx.event.EventTarget
import javafx.scene.control.Label
import java.util.stream.Stream

class Symbol(char: Char, val part: TextPart, val index: Int) : Label(char + "") {

    val line get() = part.line

    val next: Symbol?
        get() {
            val nextInPart = part.getSymbols().getOrNull(index + 1)
            if (nextInPart != null) return nextInPart

            return Stream.iterate(part.nextText()) { it?.nextText() }
                .takeWhile { it != null }
                .filter { it!!.getSymbols().isNotEmpty() }
                .findFirst()
                .map { it!!.getSymbols()[0] }
                .orElse(null)
        }

    fun comparePosition(with: Symbol): Int {
        if (!isInSameEditor(with))
            throw IllegalArgumentException("Cannot compare symbol positions because they are not in the same editor.")

        if (part === with.part) return index.compareTo(with.index)

        val lineIndexComparison = line!!.index.compareTo(with.line!!.index)
        if (lineIndexComparison != 0) return lineIndexComparison

        return part.index.compareTo(with.part.index)
    }

    fun isInSameEditor(with: Symbol): Boolean {
        return line?.editor != null && with.line?.editor != null && line!!.editor === with.line!!.editor
    }
}

fun EventTarget.symbol(char: Char, part: TextPart, index: Int, builder: Symbol.() -> Unit) =
    execBuilder(this, Symbol(char, part, index), builder)