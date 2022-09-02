package by.varyvoda.ademis.app.desktop.ui.component.editor.code

import by.varyvoda.ademis.app.desktop.ui.component.editor.code.notebar.notebar
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.partedEditor
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import javafx.event.EventTarget
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.hgrow

class CodeEditor : HBox() {

    private val noteBar = notebar { }
    val codePane = partedEditor {
        hgrow = Priority.ALWAYS
    }


}

fun EventTarget.codeEditor(init: CodeEditor.() -> Unit = {}): CodeEditor = execBuilder(this, CodeEditor(), init)
