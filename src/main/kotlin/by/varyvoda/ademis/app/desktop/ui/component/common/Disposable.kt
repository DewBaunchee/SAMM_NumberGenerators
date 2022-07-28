package by.varyvoda.ademis.app.desktop.ui.component.common

import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject

interface Disposable {

   fun disposer(): DisposeSubject

}

fun Disposable.dispose() = disposer().emit()