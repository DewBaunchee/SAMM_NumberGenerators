package by.varyvoda.ademis.app.desktop.ui.view

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.partedEditor
import tornadofx.View
import tornadofx.anchorpane
import tornadofx.anchorpaneConstraints

class AdemisDesktopView : View("Ademis") {

    override fun onDock() {
        primaryStage.isMaximized = true
    }

    override val root = anchorpane {
        val codeEditor = partedEditor {
            anchorpaneConstraints {
                topAnchor = 0
                leftAnchor = 0
                bottomAnchor = 0
                rightAnchor = 0
            }
        }
    }
}