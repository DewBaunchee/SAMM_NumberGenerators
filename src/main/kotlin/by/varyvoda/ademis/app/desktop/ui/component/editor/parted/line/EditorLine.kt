package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.line

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.Editor
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.EditorPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.LineBreakPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.TextPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.position.EditorPosition
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.AnchorPane
import tornadofx.*
import java.util.stream.Stream

class EditorLine : AnchorPane(), Disposable {

    override val disposer = DisposeSubject.create()

    var editor: Editor? = null
        internal set

    val prev get() = editor?.lines?.getOrNull(index - 1)

    val next get() = editor?.lines?.getOrNull(index + 1)

    val parts: ObservableList<EditorPart> = FXCollections.observableArrayList(LineBreakPart())

    val indexProperty = SimpleIntegerProperty(-1)
    var index by indexProperty
        internal set

    val lengthProperty = SimpleIntegerProperty(0)
    var length by lengthProperty

    private val partsBox = hbox {
        anchorpaneConstraints {
            leftAnchor = 70
            topAnchor = 0.0
            rightAnchor = 0.0
            bottomAnchor = 0.0
        }
        spacing = -2.0
    }

    init {
        disposeOnDispose { parts }
        parts.toRx(disposer).subscribe { change ->
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.removed.forEach { removed ->
                        length -= removed.getLength()
                    }

                    change.list.getOrNull(change.to)?.shiftIndex(-change.removedSize)
                }
                if (change.wasAdded()) {
                    change.addedSubList.forEachIndexed { index, added ->
                        added.line = this
                        added.index = change.from + index

                        length += added.getLength()

                        added.lengthChange().takeWhile { added.line === this }.subscribe {
                            length = length + it.new - it.prev
                            if (it.new == 0) {
                                added.remove()
                                added.dispose()
                            }
                        }
                    }

                    change.list.getOrNull(change.to)?.shiftIndex(change.addedSize)
                }
            }
        }
        partsBox.children.bind(parts) { it }
    }

    fun textParts(): List<TextPart> {
        return parts.filter { it.isText() }.map { it as TextPart }
    }

    fun start(): TextPart {
        return textParts().stream()
            .findFirst()
            .map { it as TextPart }
            .orElseThrow()
    }

    fun end(): TextPart {
        return Stream.iterate(parts.last(), EditorPart::prev)
            .takeWhile { it !== null }
            .filter { it.isText() }
            .findFirst()
            .map { it as TextPart }
            .orElseThrow()
    }

    fun br(): LineBreakPart {
        return Stream.iterate(parts.last(), EditorPart::prev)
            .takeWhile { it !== null }
            .filter { it is LineBreakPart }
            .findFirst()
            .map { it as LineBreakPart }
            .orElse(null)
    }

    fun getClosestTextPart(screenX: Double): TextPart {
        val textParts = textParts()
        return textParts.stream()
            .filter { screenX < it.localToScreen(it.boundsInLocal).maxX }
            .findFirst()
            .orElse(textParts.last())
    }

    @Suppress("Duplicates")
    fun addAfter(line: EditorLine): EditorLine {
        if (line.editor != null) throw IllegalStateException("Line is already in editor.")
        editor!!.lines.add(editor!!.lines.indexOf(this) + 1, line)
        return this
    }

    @Suppress("Duplicates")
    fun addBefore(line: EditorLine): EditorLine {
        if (line.editor != null) throw IllegalStateException("Line is already in editor.")
        editor!!.lines.add(editor!!.lines.indexOf(this), line)
        return this
    }

    @Suppress("Duplicates")
    fun remove(): Boolean {
        editor!!.lines.remove(this)
        editor = null
        index = -1
        return true
    }

    fun partAt(index: Int, orLast: Boolean = true): EditorPart {
        return getInlinePosition(index, orLast).part
    }

    fun lengthUntil(part: EditorPart): Int {
        var length = 0
        parts.stream()
            .takeWhile { it !== part }
            .forEach {
                if (it == null)
                    throw NoSuchElementException("There is no t such part to count length until it: $part")
                length += it.getLength()
            }
        return length
    }

    fun hasPrev(): Boolean {
        return prev != null
    }

    fun hasNext(): Boolean {
        return next != null
    }

    fun shiftIndex(shift: Int) {
        var current = this
        current.index += shift

        while (current.hasNext()) {
            current = current.next!!
            current.index += shift
        }
    }

    fun getInlinePosition(at: Int, orLast: Boolean = true): EditorPosition.Inline {
        var i = at
        var current: TextPart = start()
        do {
            if (i < current.getLength()) return EditorPosition.Inline(current, i)
            i -= current.getLength()
            if (current.nextTextInline() === null) break
            current = current.nextTextInline()!!
        } while (true)
        if (orLast) return EditorPosition.Inline(end(), end().getLength() - 1)
        throw IndexOutOfBoundsException("There is no part at index $at")
    }

    fun mergeTo(line: EditorLine) {
        line.end().addAllBefore(parts.dropLast(1).remove())
        remove()
        dispose()
    }

    override fun toString(): String {
        return parts.joinToString("")
    }
}

fun List<EditorPart>.remove(): List<EditorPart> {
    forEach(EditorPart::remove)
    return this
}