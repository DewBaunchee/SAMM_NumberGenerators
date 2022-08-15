package by.varyvoda.ademis.app.desktop.ui.component.editor.parted

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.util.addAndReturn
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.stream.Stream

class PartedTextEditorLine : AnchorPane(), Disposable {

    override val disposer = DisposeSubject.create()

    lateinit var editor: PartedTextEditor
        internal set

    var prev: PartedTextEditorLine? = null
        private set

    var next: PartedTextEditorLine? = null
        private set


    val parts = FXCollections.observableArrayList<PartedTextEditorLinePart>(
        execBuilder(this, LineBreakPart()) {
            hgrow = Priority.ALWAYS
        }
    )
    val start: PartedTextEditorLinePart get() = parts[0]
    val end: PartedTextEditorLinePart get() = parts.last()

    val lengthProperty = SimpleIntegerProperty(0)
    var length by lengthProperty

    private val partsBox = addAndReturn(hbox {
        anchorpaneConstraints {
            leftAnchor = 0.0
            topAnchor = 0.0
            rightAnchor = 0.0
            bottomAnchor = 0.0
        }
        spacing = PartedTextEditor.itemSpacing
    })

    init {
        parts.toRx().takeUntil(disposer).subscribe { change ->
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.removed.forEach { removed ->
                        removed.line = null
                        removed.prev = null
                        removed.next = null
                        length -= removed.getLength()
                    }
                    val prev = change.list.getOrNull(change.from - 1)
                    val next = change.list.getOrNull(change.to)
                    prev?.next = next
                    next?.prev = prev
                } else if (change.wasAdded()) {
                    val prev = change.list.getOrNull(change.from - 1)
                    val next = change.list.getOrNull(change.to)
                    change.addedSubList.forEachIndexed { index, added ->
                        added.line = this

                        if (index == 0) {
                            prev?.next = added
                            added.prev = prev
                        } else {
                            added.prev = change.addedSubList[index - 1]
                        }

                        length += added.getLength()

                        added.lengthChange().takeWhile { added.line == this }.subscribe {
                            length = length + it.new - it.prev
                            if (it.new == 0) added.remove()
                        }
                    }
                    next?.prev = change.addedSubList.last()
                }
            }
        }
        partsBox.children.bind(parts) { it }
    }

    @Suppress("Duplicates")
    fun addAfter(line: PartedTextEditorLine): PartedTextEditorLine {
        line.editor = editor
        line.prev = this
        line.next = next
        next = line
        if (line.next == null) editor.lastLine = line
        editor.linesBox.children.add(
            editor.linesBox.children.indexOf(this) + 1,
            line
        )
        return this
    }

    @Suppress("Duplicates")
    fun addBefore(line: PartedTextEditorLine): PartedTextEditorLine {
        line.editor = editor
        line.next = this
        line.prev = prev
        prev = line
        if (line.prev == null) editor.firstLine = line
        editor.linesBox.children.add(
            editor.linesBox.children.indexOf(this),
            line
        )
        return this
    }

    @Suppress("Duplicates")
    fun remove(): Boolean {
        if (prev == null && next == null) return false
        if (editor.firstLine == this) {
            editor.firstLine = next!!
        } else if (editor.lastLine == this) {
            editor.lastLine = prev!!
        }
        if (prev != null) {
            prev!!.next = next
        }
        if (next != null) {
            next!!.prev = prev
        }
        prev = null
        next = null
        editor.linesBox.children.remove(this)
        dispose()
        return true
    }

    fun partAt(index: Int, orLast: Boolean = true): PartedTextEditorLinePart {
        return getInlinePosition(index, orLast).part
    }

    fun lengthUntil(part: PartedTextEditorLinePart): Int {
        var length = 0
        iterateNullable()
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

    fun iterate(): Stream<PartedTextEditorLinePart> {
        return Stream.iterate(start, { it != null }) { it.next }
    }

    fun iterateNullable(): Stream<PartedTextEditorLinePart?> {
        return Stream.iterate(start) { it?.next }
    }

    fun getInlinePosition(at: Int, orLast: Boolean = true): PartedTextEditor.Position.Inline {
        var i = at
        var current: PartedTextEditorLinePart = start
        do {
            if (i < current.getLength()) return PartedTextEditor.Position.Inline(current, i)
            i -= current.getLength()
            if (!current.hasNextInline()) break;
            current = current.nextInline()!!
        } while (true)
        if (orLast) return PartedTextEditor.Position.Inline(end, end.getLength() - 1)
        throw IndexOutOfBoundsException("There is no part at index $at")
    }

    fun mergeTo(line: PartedTextEditorLine) {
        line.end.addAllBefore(parts.dropLast(1))
        remove()
    }

    override fun toString(): String {
        return parts.joinToString("")
    }
}