package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.position

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.line.EditorLine
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.EditorPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.TextPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.symbol.Symbol
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import rx.Observable

class EditorPosition(
    val line: EditorLine,
    val column: Int,
) {
    fun transform(transformation: EditorPosition.() -> EditorPosition): EditorPosition {
        return transformation(this)
    }

    fun handle(handler: EditorPosition.() -> Unit) = handler()

    fun part(): EditorPart {
        return line.partAt(column)
    }

    fun atBegin(): Boolean {
        return column == 0
    }

    fun atEnd(): Boolean {
        return column >= line.length - 1
    }

    fun getTrueColumn(): Int {
        return Integer.min(column, line.length - 1)
    }

    fun trueColumn(): Observable<Int> {
        return line.lengthProperty.toRx().map { if (column < it.toInt()) column else it.toInt() - 1 }
    }

    fun inlinePosition(): Observable<Inline> {
        return line.lengthProperty.toRx().map { line.getInlinePosition(getTrueColumn()) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as EditorPosition

        return this.line === other.line && this.column == other.column
    }

    override fun hashCode(): Int {
        return 31 * line.hashCode() + column
    }

    fun getInlinePosition(): Inline {
        return line.getInlinePosition(getTrueColumn())
    }

    data class Inline(val part: TextPart, val inPartPosition: Int) {

        fun handle(handler: Inline.() -> Unit) = handler()

        fun atBegin(): Boolean {
            return inPartPosition == 0
        }

        fun atEnd(): Boolean {
            return inPartPosition == part.getLength() - 1
        }

        fun getSymbol(): Symbol {
            return part.getSymbols()[inPartPosition]
        }
    }
}