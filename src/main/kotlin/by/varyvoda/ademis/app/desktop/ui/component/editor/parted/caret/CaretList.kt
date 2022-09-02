package by.varyvoda.ademis.app.desktop.ui.component.editor.parted.caret

import by.varyvoda.ademis.app.desktop.ui.component.common.Disposable
import by.varyvoda.ademis.app.desktop.ui.component.editor.parted.position.EditorPosition
import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import by.varyvoda.ademis.app.desktop.util.rx.toRx
import javafx.collections.FXCollections

class CaretList : Disposable {

    private val carets = FXCollections.observableArrayList<Caret>()

    override val disposer: DisposeSubject = DisposeSubject.create()

    init {
        disposeOnDispose { carets }
        carets.toRx(disposer).subscribe { change ->
            while (change.next()) {
                change.removed?.forEach { it.dispose() }
            }
        }
    }


    fun last(): Caret? {
        return carets.lastOrNull()
    }

    fun toggleCaret(position: EditorPosition, multiple: Boolean = false) {
        if (multiple) {
            toggleMultiple(position)
        } else {
            toggleSingle(position)
        }
    }

    private fun toggleMultiple(position: EditorPosition) {
        val caretAtPosition = findByPosition(position)
        if (caretAtPosition == null) {
            carets.add(Caret(position))
        } else {
            carets.remove(caretAtPosition)
        }
    }

    private fun toggleSingle(position: EditorPosition) {
        carets.clear()
        carets.add(Caret(position))
    }

    private fun findByPosition(position: EditorPosition): Caret? {
        return carets.find { caret -> caret.position == position }
    }

    fun forEach(action: (caret: Caret) -> Unit) {
        carets.forEach(action)
    }
}