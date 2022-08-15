package by.varyvoda.ademis.app.desktop.util.rx

import by.varyvoda.ademis.app.desktop.util.collections.Quadruple
import by.varyvoda.ademis.app.desktop.util.collections.Quintuple
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import rx.Observable
import rx.Observable.just
import rx.subjects.BehaviorSubject
import rx.subjects.ReplaySubject
import tornadofx.*

fun <V> ReadOnlyProperty<V>.toRx(): Observable<V> {
    val subject = BehaviorSubject.create(value)
    val listener = ChangeListener<V> { _, _, newValue -> subject.onNext(newValue) }
    addListener(listener)
    subject.doOnCompleted { removeListener(listener) }
    return subject.asObservable()
}

data class RxChange<V>(val prev: V, val new: V)

fun <V> ReadOnlyProperty<V>.toRxChange(): Observable<RxChange<V>> {
    val subject = ReplaySubject.create<RxChange<V>>(1)
    val listener = ChangeListener<V> { _, prev, new -> subject.onNext(RxChange(prev, new)) }
    addListener(listener)
    subject.doOnCompleted { removeListener(listener) }
    return subject.asObservable()
}

fun  ReadOnlyIntegerProperty.toRxChange(): Observable<RxChange<Int>> {
    val subject = ReplaySubject.create<RxChange<Int>>(1)
    val listener = ChangeListener<Number> { _, prev, new -> subject.onNext(RxChange(prev.toInt(), new.toInt())) }
    addListener(listener)
    subject.doOnCompleted { removeListener(listener) }
    return subject.asObservable()
}


@Suppress("UNCHECKED_CAST")
fun <V> ObservableList<V>.toRx(): Observable<ListChangeListener.Change<V>> {
    val subject = ReplaySubject.create<ListChangeListener.Change<V>>(1)
    val listener = ListChangeListener<V> { change -> subject.onNext(change as ListChangeListener.Change<V>) }

    val list = FXCollections.observableArrayList<V>()
    list.addListener(listener)
    list.addAll(this)
    list.removeListener(listener)

    addListener(listener)
    subject.doOnCompleted { removeListener(listener) }
    return subject.asObservable()
}

fun <A, B> Observable<A>.mergeToPair(func: (value: A) -> Observable<B>): Observable<Pair<A, B>> =
    switchMap { value ->
        combineLatest(
            just(value),
            func(value),
        )
    }

fun <A, B, C> Observable<Pair<A, B>>.mergeToTriple(func: (pair: Pair<A, B>) -> Observable<C>): Observable<Triple<A, B, C>> =
    switchMap { pair ->
        combineLatest(
            just(pair.first),
            just(pair.second),
            func(pair),
        )
    }

fun <A, B, C, D> Observable<Triple<A, B, C>>.mergeToQuadruple(func: (triple: Triple<A, B, C>) -> Observable<D>): Observable<Quadruple<A, B, C, D>> =
    switchMap { triple ->
        combineLatest(
            just(triple.first),
            just(triple.second),
            just(triple.third),
            func(triple),
        )
    }

fun <A, B, C, D, E> Observable<Quadruple<A, B, C, D>>.mergeToQuintuple(func: (quadruple: Quadruple<A, B, C, D>) -> Observable<E>): Observable<Quintuple<A, B, C, D, E>> =
    switchMap { quadruple ->
        combineLatest(
            just(quadruple.first),
            just(quadruple.second),
            just(quadruple.third),
            just(quadruple.fourth),
            func(quadruple),
        )
    }