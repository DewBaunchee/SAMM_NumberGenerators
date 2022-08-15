package by.varyvoda.ademis.app.desktop.ui.component.editor.parted

import by.varyvoda.ademis.app.desktop.util.rx.toRx

class LineBreakPart : PartedTextEditorLinePart(" ") {

    init {
        contentProperty.toRx()
                .takeUntil(disposer)
                .subscribe { newContent ->
                    if (newContent.length == 1) return@subscribe

                    val separated = newContent.dropLast(1)
                    content = newContent.last() + ""
                    val before = prevInline()
                    if (before == null) {
                        addBefore(PartedTextEditorLinePart(separated))
                        return@subscribe
                    }
                    before.modify { it.append(separated) }
                }
    }

    override fun isText(): Boolean {
        return false
    }
}