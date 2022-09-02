package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.component.editor.exception.NotConnected
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.line.EditorLine
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.RxChange
import by.varyvoda.ademis.app.desktop.util.rx.toRxChange
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.AnchorPane
import rx.Observable
import tornadofx.*
import java.util.stream.Stream
import kotlin.streams.toList


abstract class EditorPart : AnchorPane(), Disposable {

    final override val disposer = DisposeSubject.create()

    var line: EditorLine? = null
        internal set

    val indexProperty = SimpleIntegerProperty(-1)
    var index by indexProperty
        internal set

    init {
        style {
            borderColor += box(c("#00F5"))
        }
    }

    protected val length = SimpleIntegerProperty()

    fun fromThisInline(): List<EditorPart> {
        return Stream.iterate(this, EditorPart::nextTextInline)
            .takeWhile { it != null }
            .toList()
    }

    fun toThisInline(): List<EditorPart> {
        return Stream.iterate(this, EditorPart::prevTextInline)
            .takeWhile { it != null }
            .toList()
    }

    open fun isText(): Boolean {
        return false
    }

    @Suppress("Duplicates")
    fun addAfter(part: EditorPart): EditorPart {
        if (part.line != null) throw IllegalStateException("Part is already in line.")
        line!!.parts.add(line!!.parts.indexOf(this) + 1, part)
        return this
    }

    @Suppress("Duplicates")
    fun addBefore(part: EditorPart): EditorPart {
        if (part.line != null) throw IllegalStateException("Part is already in line.")
        line!!.parts.add(line!!.parts.indexOf(this), part)
        return this
    }

    fun addAllBefore(parts: List<EditorPart>): EditorPart {
        parts.forEach(this::addBefore)
        return this
    }

    @Suppress("Duplicates")
    fun remove(): Boolean {
        line!!.parts.remove(this)
        line = null
        return true
    }

    fun prev(inline: Boolean = false): EditorPart? {
        return requireLine().parts.getOrNull(index - 1)
            ?: if (inline) null else requireLine().prev?.parts?.lastOrNull()
    }

    fun next(inline: Boolean = false): EditorPart? {
        return requireLine().parts.getOrNull(index + 1)
            ?: if (inline) null else requireLine().next?.parts?.getOrNull(0)
    }

    fun hasPrev(inline: Boolean = false): Boolean {
        return prev(inline) != null
    }

    fun hasNext(inline: Boolean = false): Boolean {
        return next(inline) != null
    }

    fun prevTextInline(): TextPart? {
        return Stream.iterate(prev(true)) { it?.prev(true) }.findText()
    }

    fun nextText(): TextPart? {
        return Stream.iterate(next()) { it?.next() }.findText()
    }

    fun nextTextInline(): TextPart? {
        return Stream.iterate(next(true)) { it?.next(true) }.findText()
    }

    fun shiftIndex(shift: Int) {
        var current = this
        current.index += shift

        while (current.hasNext()) {
            current = current.next()!!
            current.index += shift
        }
    }

    fun getLength(): Int {
        return length.get()
    }

    fun lengthChange(): Observable<RxChange<Int>> {
        return length.toRxChange(disposer)
    }

    private fun requireLine(): EditorLine {
        if (line == null) throw NotConnected("Part is not connected!")
        return line!!
    }
}

private fun Stream<EditorPart?>.findText(): TextPart? =
    takeWhile { it != null }
        .filter { it!!.isText() }
        .findFirst()
        .map { it as TextPart }
        .orElse(null)