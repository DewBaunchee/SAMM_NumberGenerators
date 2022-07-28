package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.model

import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.paint.Color
import tornadofx.*

class PartedText(lines: List<Line> = emptyList()) {

    private val endPart: Part = Part(" ", c("red"))

    private var endLine: Line? = null

    val lines: ObservableList<Line> = FXCollections.observableArrayList(lines)

    init {
        this.lines.toRx()
            .switchMap {
                endLine?.parts?.remove(endPart)
                endLine = if (it.list.isEmpty()) Line() else it.list.last()
                return@switchMap endLine!!.parts.toRx()
            }
            .subscribe {
                if(it.list.last() === endPart) return@subscribe
                it.list.remove(endPart)
                it.list.add(endPart)
            }
    }

    class Line(parts: List<Part> = emptyList()) {

        val parts: ObservableList<Part> = FXCollections.observableArrayList(parts)
    }

    class Part(
        content: String,
        val color: Color
    ) {
        val contentProperty = SimpleStringProperty(content)
        var content by contentProperty

        fun modify(modifier: (contentBuilder: StringBuilder) -> Boolean): Boolean {
            val contentBuilder = StringBuilder(content)
            val modified = modifier(contentBuilder)
            content = contentBuilder.toString()
            return modified
        }
    }
}