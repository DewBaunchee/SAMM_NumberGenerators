package by.varyvoda.ademis.app.desktop.ui.view

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.line.EditorLine
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.LineBreakPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part.TextPart
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.partedEditor
import tornadofx.View
import tornadofx.anchorpane
import tornadofx.anchorpaneConstraints

class AdemisDesktopView : View("Ademis") {

    override fun onDock() {
        primaryStage.isMaximized = true
    }

    override val root = anchorpane {
        partedEditor {
            anchorpaneConstraints {
                topAnchor = 0
                leftAnchor = 0
                bottomAnchor = 0
                rightAnchor = 0
            }
            lines.setAll(
                listOf(
                    EditorLine().also {
                        it.parts.setAll(
                            listOf(
                                TextPart("f"),
                                TextPart("f"),
                                TextPart("f"),
                                LineBreakPart()
                            )
                        )
                    },
                    EditorLine().also { it.parts.setAll(listOf(TextPart("fff"), LineBreakPart())) },
                )
            )
        }
    }
}