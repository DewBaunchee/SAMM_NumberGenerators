package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.event

import by.varyvoda.ademis.app.desktop.ui.behaviour.keyevent.KeyEventHandlerHolder
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.Editor
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.Caret
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret.CaretList
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.line.EditorLine
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.line.remove
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.position.EditorPosition
import by.varyvoda.ademis.app.desktop.util.rx.toRxChange
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

typealias KeyEventHandler = (caret: Caret, event: KeyEvent) -> Unit

class EventAdapter(
    editor: Editor,
    carets: CaretList
) {

    companion object {

        private val handlers =
            KeyEventHandlerHolder<KeyEventHandler> { caret, event ->
                if (event.text.isNotEmpty()) {
                    caret.position.getInlinePosition().handle {
                        part.modify {
                            it.insert(inPartPosition, event.text)
                            return@modify
                        }
                        caret.move(event.text.length)
                    }
                }
            }
                .register(KeyCode.BACK_SPACE) { caret, _ ->
                    caret.position.handle {
                        if (getTrueColumn() == 0) {
                            if (!line.hasPrev()) return@handle

                            val prev = line.prev!!
                            val newColumn = prev.length - 1
                            line.mergeTo(prev)
                            caret.position = EditorPosition(prev, newColumn)
                            return@handle
                        }
                        line.getInlinePosition(getTrueColumn() - 1).handle {
                            if (getTrueColumn() > line.length - 1) {
                                caret.position = EditorPosition(line, line.length - 1)
                            } else {
                                caret.move(-1)
                            }
                            part.modify {
                                it.deleteCharAt(inPartPosition)
                                return@modify
                            }
                        }
                    }
                }
                .register(KeyCode.DELETE) { caret, _ ->
                    caret.position.handle {
                        if (column > line.length - 2) {
                            if (!line.hasNext()) return@handle

                            val next = line.next!!
                            next.mergeTo(line)
                            return@handle
                        }
                        getInlinePosition().handle {
                            part.modify {
                                it.deleteCharAt(inPartPosition)
                                return@modify
                            }
                            caret.skipBlinking()
                        }
                    }
                }
                .register(KeyCode.UP) { caret, _ -> caret.up() }
                .register(KeyCode.DOWN) { caret, _ -> caret.down() }
                .register(KeyCode.RIGHT) { caret, _ -> caret.move(1) }
                .register(KeyCode.LEFT) { caret, _ -> caret.move(-1) }
                .register(KeyCode.ENTER) { caret, _ ->
                    caret.position.handle {
                        line.addAfter(EditorLine())
                        getInlinePosition().handle inline@{
                            if (part.isText()) {
                                if (inPartPosition == 0) {
                                    line.next!!.start().addAllBefore(part.fromThisInline().dropLast(1).remove())
                                    return@inline
                                } else {
                                    part.split(inPartPosition)
                                }
                            }
                            line.next!!.start().addAllBefore(part.fromThisInline().drop(1).dropLast(1).remove())
                        }
                    }

                    caret.move { EditorPosition(line.next!!, 0) }
                }
    }

    init {
        val sceneHandler = EventHandler<KeyEvent> { event ->
            val handler = handlers.getSuitableHandler(event)
            carets.forEach { handler(it, event) }
        }
        editor.sceneProperty().toRxChange(initialPrev = null, editor.disposer)
            .subscribe {
                it.prev?.removeEventHandler(KeyEvent.KEY_PRESSED, sceneHandler)
                it.new?.addEventHandler(KeyEvent.KEY_PRESSED, sceneHandler)
            }
    }
}