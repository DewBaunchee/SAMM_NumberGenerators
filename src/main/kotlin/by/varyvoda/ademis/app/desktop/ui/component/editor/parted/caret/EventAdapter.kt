package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.PartedTextEditor
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

class EventAdapter(editor: PartedTextEditor,
                   carets: CaretSet) {

    init {
        editor.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            carets.forEach {
                when (event.code) {
                    KeyCode.BACK_SPACE -> it.backspace()
                    KeyCode.DELETE -> it.delete()
                    KeyCode.UP -> it.up()
                    KeyCode.DOWN -> it.down()
                    KeyCode.RIGHT -> it.right()
                    KeyCode.LEFT -> it.left()
                    else -> it.addText(event.text)
                }
            }
        }
    }
}