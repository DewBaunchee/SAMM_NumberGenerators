package by.varyvoda.ademis.app.desktop.ui.component.editor.code

import by.varyvoda.ademis.app.desktop.ui.component.editor.code.notebar.notebar
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.partedEditor
import by.varyvoda.ademis.app.desktop.ui.util.addAndReturn
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import javafx.event.EventTarget
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

class CodeEditor : HBox() {

    private val noteBar = addAndReturn(notebar { })
    val codePane = addAndReturn(partedEditor {
        hgrow = Priority.ALWAYS
    })


}

fun EventTarget.codeEditor( init: CodeEditor.() -> Unit = {}): CodeEditor = execBuilder(this, CodeEditor(), init)
