package by.varyvoda.ademis.app.desktop.ui.component.common

import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject

interface Disposable {

   val disposer: DisposeSubject

   fun dispose() {
      disposer.emit()
   }
}
