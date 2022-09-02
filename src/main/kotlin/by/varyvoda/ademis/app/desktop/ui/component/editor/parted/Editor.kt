package by.varyvoda.ademis.app.desktop.ui.component.editor.parted

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.CaretList
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.event.EventAdapter
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.line.EditorLine
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.EditorPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.position.EditorPosition
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.selection.Selection
import by.varyvoda.ademis.app.desktop.ui.component.scroll.ScrollPane
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import by.varyvoda.ademis.app.desktop.util.rx.toRxChange
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import tornadofx.*

class Editor : ScrollPane(), Disposable {

    companion object {
        const val itemSpacing = -1.0
        const val EOL = "\n"
    }

    val carets: CaretList = CaretList()

    val selection = Selection()

    init {
        disposeOnDispose { listOf(selection, carets) }
        addEventHandler(MouseEvent.MOUSE_PRESSED) {
            carets.toggleCaret(getPosition(it.sceneX, it.sceneY))
            selection.start(carets.last()!!)
        }
        val draggedHandler = EventHandler<MouseEvent> {
            if (selection.isRunning())
                carets.last()!!.move { getPosition(it.sceneX, it.sceneY) }
        }
        val releasedHandler = EventHandler<MouseEvent> {
            selection.stop()
        }
        sceneProperty().toRxChange(initialPrev = null, disposer)
            .takeUntil(disposer)
            .doOnCompleted {
                scene?.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler)
                scene?.removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler)
            }
            .subscribe {
                it.prev?.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler)
                it.new?.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler)

                it.prev?.removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler)
                it.new?.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler)
            }
    }

    val lines: ObservableList<EditorLine> = FXCollections.observableArrayList(
        EditorLine()
    )
    val first: EditorLine get() = lines.first()
    val last: EditorLine get() = lines.last()

    private val linesBox = vbox {
        scrollable = this

        style {
            borderColor += box(c("red"))
        }

        spacing = itemSpacing
    }

    init {
        style {
            borderColor += box(c("green"))
        }
        disposeOnDispose { lines }

        widthProperty().toRx(disposer).subscribe { linesBox.minWidth = it }

        lines.toRx(disposer).subscribe { change ->
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.list.getOrNull(change.to)?.shiftIndex(-change.removedSize)
                }
                if (change.wasAdded()) {
                    change.addedSubList.forEachIndexed { index, added ->
                        added.editor = this
                        added.index = change.from + index
                    }

                    change.list.getOrNull(change.to)?.shiftIndex(change.addedSize)
                }
            }
        }
        linesBox.children.bind(lines) { it }

        EventAdapter(this, carets)
        cursor = Cursor.TEXT
    }

    fun getClosestLine(screenY: Double): EditorLine {
        return lines.stream()
            .filter { screenY < it.localToScreen(it.boundsInLocal).maxY }
            .findFirst()
            .orElse(lines.last())
    }

    private fun getPosition(sceneX: Double, sceneY: Double): EditorPosition {
        val target = getClosestLine(sceneY).getClosestTextPart(sceneX).getClosestSymbol(sceneX)
        target.requestFocus()

        var part: EditorPart = target.part
        var atPartPosition: Int = target.index
        if (sceneX > target.localToScreen(target.boundsInLocal).centerX) {
            if (target.index < target.part.content.length - 1) {
                atPartPosition++
            } else if (part.hasNext(true) && part.next()!!.isText()) {
                part = part.next()!!
                atPartPosition = 0
            }
        }
        return EditorPosition(part.line!!, part.line!!.lengthUntil(part) + atPartPosition)
    }
}

fun EventTarget.partedEditor(init: Editor.() -> Unit = {}): Editor =
    execBuilder(this, Editor(), init)