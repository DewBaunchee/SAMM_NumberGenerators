package by.varyvoda.ademis.app.desktop.ui.component.editor.parted

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.util.addAndReturn
import by.varyvoda.ademis.app.desktop.util.rx.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import rx.Observable
import tornadofx.*
import java.util.stream.Stream
import kotlin.streams.toList


open class PartedTextEditorLinePart(initialContent: String) : AnchorPane(), Disposable {

    override val disposer = DisposeSubject.create()

    var line: PartedTextEditorLine? = null
        internal set

    var prev: PartedTextEditorLinePart? = null
        internal set

    var next: PartedTextEditorLinePart? = null
        internal set

    val contentProperty = SimpleStringProperty(initialContent)
    var content by contentProperty

    private val length = SimpleIntegerProperty()

    private val textBox = addAndReturn(hbox {
        anchorpaneConstraints {
            leftAnchor = 0.0
            topAnchor = 0.0
            rightAnchor = 0.0
            bottomAnchor = 0.0
        }
        onMousePressed = EventHandler {
            if (it.target === this) lastSymbol().onMousePressed.handle(it)
        }
        spacing = PartedTextEditor.itemSpacing
    })

    init {
        contentProperty.toRx().subscribe { content ->
            textBox.children.setAll(
                content
                    .split("").drop(1).dropLast(1)
                    .mapIndexed { index, symbol ->
                        val text = text(symbol) {
                            style {
                                fontFamily = "Courier New"
                                fontSize = 100.px
                            }
                        }
                        text.onMousePressed = EventHandler {
                            requestFocus()
                            line!!.editor.carets.toggleCaret(
                                PartedTextEditor.Position(line!!, line!!.lengthUntil(this) + index)
                            )
                        }
                        return@mapIndexed (text)
                    }
            )
            length.set(content.length)
        }
    }

    fun symbolBounds(position: Int): Observable<Bounds> {
        return textBox.children.toRx()
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

    fun split(at: Int) {
        val left = content.substring(0, at)
        val right = content.substring(at)

        content = left
        addAfter(PartedTextEditorLinePart(right))
    }

    fun fromThis(): List<PartedTextEditorLinePart> {
        return Stream.iterate(this, PartedTextEditorLinePart::nextInline)
            .takeWhile { it != null }
            .toList()
    }

    fun toThis(): List<PartedTextEditorLinePart> {
        return Stream.iterate(this, PartedTextEditorLinePart::prevInline)
            .takeWhile { it != null }
            .toList()
    }

    open fun isText(): Boolean {
        return true
    }

    @Suppress("Duplicates")
    fun addAfter(part: PartedTextEditorLinePart): PartedTextEditorLinePart {
        if (part.line != null) part.line!!.parts.remove(part)
        line!!.parts.add(line!!.parts.indexOf(this) + 1, part)
        return this
    }

    @Suppress("Duplicates")
    fun addBefore(part: PartedTextEditorLinePart): PartedTextEditorLinePart {
        if (part.line != null) part.line!!.parts.remove(part)
        line!!.parts.add(line!!.parts.indexOf(this), part)
        return this
    }

    fun addAllBefore(parts: List<PartedTextEditorLinePart>): PartedTextEditorLinePart {
        parts.forEach(this::addBefore)
        return this
    }

    @Suppress("Duplicates")
    fun remove(): Boolean {
        line!!.parts.remove(this)
        dispose()
        return true
    }

    fun hasPrev(): Boolean {
        return prev != null
    }

    fun hasNext(): Boolean {
        return next != null
    }

    fun prevInline(): PartedTextEditorLinePart? {
        return Stream.iterate(prev) { it?.prev }
            .takeWhile { it?.line == line }
            .filter { it!!.isText() }
            .findFirst()
            .orElse(null)
    }

    fun hasPrevInline(): Boolean {
        return prevInline() != null
    }

    fun nextInline(): PartedTextEditorLinePart? {
        return Stream.iterate(next) { it?.next }
            .takeWhile { it?.line == line }
            .filter { it!!.isText() }
            .findFirst()
            .orElse(null)
    }

    fun hasNextInline(): Boolean {
        return nextInline() != null
    }

    fun getLength(): Int {
        return length.get()
    }

    fun lengthChange(): Observable<RxChange<Int>> {
        return length.toRxChange().takeUntil(disposer)
    }

    fun modify(modifier: (contentBuilder: StringBuilder) -> Unit) {
        val contentBuilder = StringBuilder(content)
        modifier(contentBuilder)
        content = contentBuilder.toString()
    }

    fun symbols(): List<Text> {
        return textBox.children.map { it as Text }
    }

    fun lastSymbol(): Text {
        return symbols().last()
    }

    override fun toString(): String {
        return content
    }
}