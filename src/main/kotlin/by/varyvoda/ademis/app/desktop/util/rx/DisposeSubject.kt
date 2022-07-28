package by.varyvoda.ademis.app.desktop.util.rx

import rx.Observable
import rx.subjects.PublishSubject

class DisposeSubject private constructor(internalSubject: PublishSubject<Nothing?>, f: OnSubscribe<Nothing?>?) : Observable<Nothing?>(f) {

    private val _internal = internalSubject

    companion object {

        fun create(): DisposeSubject {
            val internalSubject = PublishSubject.create<Nothing>()
            return DisposeSubject(internalSubject, internalSubject::subscribe)
        }
    }

    fun emit() {
        _internal.onNext(null)
        _internal.onCompleted()
    }

}