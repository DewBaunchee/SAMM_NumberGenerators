package by.varyvoda.ademis.app.desktop.ui.view

import by.varyvoda.ademis.app.desktop.ui.component.editor.code.codeEditor
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.model.PartedText
import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.anchorpane
import tornadofx.anchorpaneConstraints

class AdemisDesktopView : View("Ademis") {

    override fun onDock() {
        primaryStage.isMaximized = true
    }

    override val root = anchorpane {
        codeEditor(
            PartedText(
                listOf(
                    PartedText.Line(
                        listOf(
                            PartedText.Part("123", Color.RED),
                            PartedText.Part("abc", Color.BLUE),
                            PartedText.Part("qwe", Color.BLACK),
                        )
                    ),
                    PartedText.Line(
                        listOf(
                            PartedText.Part("123", Color.RED),
                            PartedText.Part("abc", Color.BLUE),
                            PartedText.Part("qwe", Color.BLACK),
                        )
                    )
                )
            )
        ) {
            anchorpaneConstraints {
                topAnchor = 0
                leftAnchor = 0
                bottomAnchor = 0
                rightAnchor = 0
            }
        }
    }
}