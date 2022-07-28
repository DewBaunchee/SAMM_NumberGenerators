package by.varyvoda.ademis.app.desktop.ui.component.editor.parted

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.component.common.dispose
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.Caret
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.CaretSet
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.EventAdapter
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.model.PartedText
import by.varyvoda.ademis.app.desktop.ui.util.addAndReturn
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.combineLatest
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.Cursor
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import rx.Observable
import tornadofx.*

class PartedTextEditor(partedText: PartedText) : VBox() {

    companion object {
        private const val itemSpacing = -2.0
    }

    val textProperty = SimpleObjectProperty(partedText)
    var text by textProperty

    val carets: CaretSet = CaretSet()

    init {
        EventAdapter(this, carets)
        cursor = Cursor.TEXT
        spacing = itemSpacing
        textProperty.toRx()
            .switchMap { it.lines.toRx() }
            .subscribe { lines ->
                children.forEach { child -> (child as Line).dispose() }
                val newLines = mutableListOf<Line>()
                var prev: Line? = null
                lines.list.forEach {
                    val newLine = Line(it, this, prev)
                    newLines.add(newLine)
                    newLine.prev = prev
                    prev?.next = newLine
                    prev = newLine
                }
                children.setAll(newLines)
            }

        onMouseClicked = EventHandler {
            when (it.target) {
                is Caret -> return@EventHandler
                is Text -> return@EventHandler
                is PartedTextEditor -> {
                    val line = children.last() as Line
                    val part = line.partsBox.children.last() as Part
                    part.hBox.children.last().onMouseClicked.handle(it)
                }
            }
        }
    }

    class Line(
        partedTextLine: PartedText.Line,
        val editor: PartedTextEditor,
        prev: Line?
    ) : AnchorPane(), Disposable {

        var prev: Line? = prev
            internal set

        var next: Line? = null
            internal set

        internal val partsBox = addAndReturn(hbox {
            anchorpaneConstraints {
                leftAnchor = 0.0
                topAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor = 0.0
            }
            spacing = itemSpacing
        })

        private val destroy = DisposeSubject.create()

        init {
            partedTextLine.parts.toRx()
                .takeUntil(destroy)
                .subscribe { parts ->
                    partsBox.children.forEach { child -> (child as Part).dispose() }
                    val newParts = mutableListOf<Part>()
                    var prev: Part? = this.prev?.lastPart()
                    parts.list.forEach {
                        val newPart = Part(it, this)
                        newParts.add(newPart)
                        newPart.prev = prev
                        prev?.next = newPart
                        prev = newPart
                    }
                    partsBox.children.setAll(newParts)
                }
        }

        @Suppress("UNCHECKED_CAST")
        fun parts(): ObservableList<Part> {
            return partsBox.children as ObservableList<Part>
        }

        fun lastPart(): Part {
            return parts().last() as Part
        }

        override fun disposer() = destroy
    }

    class Part(
        val textPart: PartedText.Part,
        val line: Line,
    ) : AnchorPane(), Disposable {

        var prev: Part? = null
            internal set

        var next: Part? = null
            internal set

        internal val hBox = addAndReturn(hbox {
            anchorpaneConstraints {
                leftAnchor = 0.0
                topAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor = 0.0
            }
            spacing = itemSpacing
        })

        private val content by textPart.contentProperty

        private val destroy = DisposeSubject.create()

        init {
            textPart.contentProperty.toRx()
                .takeUntil(destroy)
                .subscribe { content ->
                    hBox.children.setAll(
                        content
                            .split("")
                            .drop(1)
                            .dropLast(1)
                            .mapIndexed { index, symbol ->
                                val text = text(symbol)
                                text.onMouseClicked = EventHandler {
                                    requestFocus()
                                    line.editor.carets.toggleCaret(getLocation(index))
                                }
                                return@mapIndexed (text)
                            }
                    )
                }
            hBox.children.style {
                fill = textPart.color
            }
        }

        fun symbolBounds(position: Int): Observable<Bounds> {
            return hBox.children.toRx()
                .filter { position < it.list.size }
                .map { it.list[position] as Text }
                .switchMap {
                    combineLatest(
                        it.layoutXProperty().toRx(),
                        it.layoutYProperty().toRx(),
                        it.layoutBoundsProperty().toRx(),
                    )
                }
                .map { BoundingBox(it.first.toDouble(), it.second.toDouble(), it.third.width, it.third.height) }
        }

        fun nextInline(): Part? {
            if (next?.line == line) return next
            return null
        }

        fun prevInline(): Part? {
            if (prev?.line == line) return next
            return null
        }

        fun getLength(): Int {
            return textPart.content.length
        }

        private fun getLocation(symbolPosition: Int): Position {
            return Position(line, this, symbolPosition)
        }

        override fun disposer(): DisposeSubject = destroy
    }

    class Position(
        val line: Line,
        val part: Part,
        val symbolPosition: Int
    ) {
        fun transform(transformation: Position.() -> Position): Position {
            return transformation(this)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false
            other as Position

            return this.line === other.line &&
                    this.part === other.part &&
                    this.symbolPosition == other.symbolPosition
        }

        override fun hashCode(): Int {
            var result = line.hashCode()
            result = 31 * result + part.hashCode()
            result = 31 * result + symbolPosition
            return result
        }
    }
}

fun EventTarget.partedEditor(partedText: PartedText, init: PartedTextEditor.() -> Unit = {}): PartedTextEditor =
    execBuilder(this, PartedTextEditor(partedText), init)