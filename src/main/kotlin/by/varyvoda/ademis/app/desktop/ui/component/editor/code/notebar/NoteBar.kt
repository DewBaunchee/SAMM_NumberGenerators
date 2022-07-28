package by.varyvoda.ademis.app.desktop.ui.component.editor.code.notebar

import by.varyvoda.ademis.app.desktop.ui.util.execBuilder
import javafx.event.EventTarget
import javafx.scene.layout.VBox

class NoteBar : VBox()

fun EventTarget.notebar(init: NoteBar.() -> Unit): NoteBar = execBuilder(this, NoteBar(), init)