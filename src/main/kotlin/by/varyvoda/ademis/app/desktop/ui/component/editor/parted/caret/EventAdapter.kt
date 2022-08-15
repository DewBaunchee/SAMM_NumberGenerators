package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.PartedTextEditor
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.PartedTextEditorLine
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

typealias KeyEventHandler = (caret: Caret, event: KeyEvent) -> Unit

class EventAdapter(
    editor: PartedTextEditor,
    carets: CaretSet
) {

    companion object {

        private val handlers =
            mapOf<KeyCode, KeyEventHandler>(
                KeyCode.BACK_SPACE to { caret, _ ->
                    caret.position.handle {
                        if (getTrueColumn() == 0) {
                            if (!line.hasPrev()) return@handle

                            val prev = line.prev!!
                            val newColumn = prev.length - 1
                            line.mergeTo(prev)
                            caret.position = PartedTextEditor.Position(prev, newColumn)
                            return@handle
                        }
                        line.getInlinePosition(getTrueColumn() - 1).handle {
                            part.modify {
                                it.deleteCharAt(inPartPosition)
                                return@modify
                            }

                            if (getTrueColumn() == line.length - 1) {
                                caret.position = PartedTextEditor.Position(line, line.length - 1)
                            } else {
                                caret.move(-1)
                            }
                        }
                    }
                },
                KeyCode.DELETE to { caret, _ ->
                    caret.position.handle {
                        if (column > line.length - 2) {
                            if (!line.hasNext()) return@handle

                            val next = line.next!!
                            next.mergeTo(line)
                            caret.position = PartedTextEditor.Position(line, line.length - 1)
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
                },
                KeyCode.UP to { caret, _ -> caret.up() },
                KeyCode.DOWN to { caret, _ -> caret.down() },
                KeyCode.RIGHT to { caret, _ -> caret.move(1) },
                KeyCode.LEFT to { caret, _ -> caret.move(-1) },
                KeyCode.ENTER to { caret, _ ->
                    caret.position.handle {
                        line.addAfter(PartedTextEditorLine())
                        getInlinePosition().handle inline@{
                            if (part.isText()) {
                                if (inPartPosition == 0) {
                                    line.next!!.start.addAllBefore(part.fromThis())
                                    return@inline
                                } else {
                                    part.split(inPartPosition)
                                }
                            }
                            line.next!!.start.addAllBefore(part.fromThis().drop(1))
                        }
                    }

                    caret.move {
                        return@move PartedTextEditor.Position(line.next!!, 0)
                    }
                }
            )

        private val defaultHandler: KeyEventHandler =
            { caret, event ->
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
    }

    init {
        editor.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            val handler = handlers.getOrDefault(event.code, defaultHandler)
            carets.forEach { handler(it, event) }
        }
    }
}
