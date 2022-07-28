package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret

import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.PartedTextEditor
import javafx.collections.FXCollections
import javafx.collections.WeakListChangeListener

class CaretSet {

    private val carets = FXCollections.observableArrayList<Caret>()

    init {
        carets.addListener(WeakListChangeListener { change ->
            while (change.next()) {
                change.removed?.forEach { it.dispose() }
            }
        })
    }

    fun toggleCaret(position: PartedTextEditor.Position, multiple: Boolean = false) {
        if (multiple) {
            toggleMultiple(position)
        } else {
            toggleSingle(position)
        }
    }

    private fun toggleMultiple(position: PartedTextEditor.Position) {
        val caretAtPosition = findByPosition(position)
        if (caretAtPosition == null) {
            carets.add(Caret(position))
        } else {
            carets.remove(caretAtPosition)
        }
    }

    private fun toggleSingle(position: PartedTextEditor.Position) {
        carets.clear()
        carets.add(Caret(position))
    }

    private fun findByPosition(position: PartedTextEditor.Position): Caret? {
        return carets.find { caret -> caret.position == position }
    }

    fun forEach(action: (caret: Caret) -> Unit) {
        carets.forEach(action)
    }
}