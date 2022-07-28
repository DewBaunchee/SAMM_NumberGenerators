package by.varyvoda.ademis.app.desktop.ui.component.editor.code

import by.varyvoda.ademis.app.desktop.ui.component.editor.code.notebar.notebar
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.model.PartedText
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.partedEditor
import by.varyvoda.ademis.app.desktop.ui.util.addAndReturn
import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import javafx.event.EventTarget
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*

class CodeEditor(partedText: PartedText) : HBox() {

    private val noteBar = addAndReturn(notebar { })
    private val codePane = addAndReturn(partedEditor(partedText) {
        hgrow = Priority.ALWAYS
        style {
            fontFamily = "Courier New"
            fontSize = 100.px
        }
    })

    var text by codePane.textProperty

}

fun EventTarget.codeEditor(partedText: PartedText, init: CodeEditor.() -> Unit = {}): CodeEditor = execBuilder(this, CodeEditor(partedText), init)
