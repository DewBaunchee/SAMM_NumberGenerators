package by.varyvoda.ademis.app.desktop.ui.component.common

import by.varyvoda.ademis.app.desktop.util.rx.DisposeSubject
import rx.subjects.Subject

interface Disposable {

    val disposer: DisposeSubject

    fun dispose() {
        disposer.emit()
    }

    fun disposeOnDispose(disposables: () -> List<Disposable>) {
        disposer.take(1).subscribe { disposables().forEach(Disposable::dispose) }
    }

    fun completeOnDispose(completable: () -> List<Subject<Any, Any>>) {
        disposer.take(1).subscribe { completable().forEach(Subject<Any, Any>::onCompleted) }
    }
}
