package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.style

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.cssclass

class PartedTextEditorStyle : Stylesheet() {

    companion object {
        val selected by cssclass()
    }

    init {
        selected {
            backgroundColor += Color.DARKRED
        }
    }
}