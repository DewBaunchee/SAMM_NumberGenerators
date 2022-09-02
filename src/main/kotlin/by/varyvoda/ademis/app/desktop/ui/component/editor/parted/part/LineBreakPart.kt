package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.part

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.Editor
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.scene.layout.Priority
import tornadofx.hgrow

class LineBreakPart : TextPart(Editor.EOL) {

    init {
        hgrow = Priority.ALWAYS
        val symbol = createSymbol(' ', 0)
        symbol.hgrow = Priority.ALWAYS
        symbol.maxWidth = Double.MAX_VALUE
        symbols.add(symbol)
        length.set(content.length)

        contentProperty.toRx(disposer)
            .skip(1)
            .subscribe { newContent ->
                if (newContent.length == 1) return@subscribe

                val separated = newContent.dropLast(1)
                content = newContent.last() + ""
                val before = prevTextInline()
                if (before == null) {
                    addBefore(TextPart(separated))
                    return@subscribe
                }
                before.modify { it.append(separated) }
            }
    }

    override fun handleContent(content: String) {
    }
}