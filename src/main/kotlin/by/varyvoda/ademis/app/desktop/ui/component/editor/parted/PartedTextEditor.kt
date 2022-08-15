package by.varyvoda.ademis.app.desktop.ui.component.editor.parted

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.CaretSet
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.EventAdapter
import by.varyvoda.ademis.app.desktop.ui.util.addAndReturn
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import by.varyvoda.ademis.app.desktop.util.rx.*
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Cursor
import javafx.scene.layout.AnchorPane
import rx.Observable
import tornadofx.*
import java.lang.Integer.min
import java.util.stream.Stream
import kotlin.streams.toList

class PartedTextEditor : AnchorPane() {

    companion object {
        const val itemSpacing = -2.0
        private const val lineSep = "\n\r"
    }

    val carets: CaretSet = CaretSet()

    var firstLine = PartedTextEditorLine()
        internal set

    var lastLine = firstLine
        internal set

    internal val linesBox = addAndReturn(vbox {
        spacing = itemSpacing
        anchorpaneConstraints {
            topAnchor = 0
            rightAnchor = 0
            bottomAnchor = 0
            leftAnchor = 0
        }
    })

    init {
        firstLine.editor = this
        linesBox.children.setAll(listOf(firstLine))

        EventAdapter(this, carets)
        cursor = Cursor.TEXT

        onMousePressed = EventHandler {
            if (it.target === linesBox) {
                lastLine.end.lastSymbol().onMousePressed.handle(it)
            }
        }
    }

    private fun lines(): List<PartedTextEditorLine> {
        return Stream.iterate(firstLine, PartedTextEditorLine::hasNext) { it.next }.toList()
    }

    fun setLines(lines: List<PartedTextEditorLine>) {
        firstLine = if (lines.isEmpty()) PartedTextEditorLine() else lines[0]

        lastLine = firstLine
        linesBox.children.setAll(listOf(firstLine))
        for (i in 1..lines.lastIndex) {
            lastLine.addAfter(lines[i])
        }
    }

//    fun text(): Observable<String> {
//        return lines.toRx()
//            .switchMap { lines ->
//                combineLatest(lines.list.map { line ->
//                    line.parts.toRx().switchMap { parts ->
//                        combineLatest(parts.list.map { part -> part.contentProperty.toRx() })
//                            .map { it.joinToString(separator = "") }
//                    }
//                })
//            }
//            .map { it.joinToString(separator = lineSep) }
//
//    }

    class Position(
            val line: PartedTextEditorLine,
            val column: Int,
    ) {
        fun transform(transformation: Position.() -> Position): Position {
            return transformation(this)
        }

        fun handle(handler: Position.() -> Unit) = handler()

        fun part(): PartedTextEditorLinePart {
            return line.partAt(column)
        }

        fun atBegin(): Boolean {
            return column == 0
        }

        fun atEnd(): Boolean {
            return column >= line.length - 1
        }

        fun getTrueColumn(): Int {
            return min(column, line.length - 1)
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
            other as Position

            return this.line === other.line && this.column == other.column
        }

        override fun hashCode(): Int {
            return 31 * line.hashCode() + column
        }

        fun getInlinePosition(): Inline {
            return line.getInlinePosition(getTrueColumn())
        }

        data class Inline(val part: PartedTextEditorLinePart, val inPartPosition: Int) {

            fun handle(handler: Inline.() -> Unit) = handler()

            fun atBegin(): Boolean {
                return inPartPosition == 0
            }

            fun atEnd(): Boolean {
                return inPartPosition == part.getLength() - 1
            }
        }
    }
}

fun EventTarget.partedEditor(init: PartedTextEditor.() -> Unit = {}): PartedTextEditor =
        execBuilder(this, PartedTextEditor(), init)