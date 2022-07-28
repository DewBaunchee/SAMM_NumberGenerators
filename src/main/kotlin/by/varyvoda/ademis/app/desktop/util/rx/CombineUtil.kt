package by.varyvoda.ademis.app.desktop.util.rx

import by.varyvoda.ademis.app.desktop.util.collections.Quadruple
import by.varyvoda.ademis.app.desktop.util.collections.Quintuple
import rx.Observable

@Suppress("UNCHECKED_CAST")
fun <A, B> combineLatest(
    first: Observable<A>,
    second: Observable<B>
): Observable<Pair<A, B>> =
    Observable.combineLatest(listOf(first, second)) { args ->
        Pair(args[0] as A, args[1] as B)
    }

@Suppress("UNCHECKED_CAST")
fun <A, B, C> combineLatest(
    first: Observable<A>,
    second: Observable<B>,
    third: Observable<C>
): Observable<Triple<A, B, C>> =
    Observable.combineLatest(listOf(first, second, third)) { args ->
        Triple(args[0] as A, args[1] as B, args[2] as C)
    }

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D> combineLatest(
    first: Observable<A>,
    second: Observable<B>,
    third: Observable<C>,
    fourth: Observable<D>
): Observable<Quadruple<A, B, C, D>> =
    Observable.combineLatest(listOf(first, second, third, fourth)) { args ->
        Quadruple(args[0] as A, args[1] as B, args[2] as C, args[3] as D)
    }

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E> combineLatest(
    first: Observable<A>,
    second: Observable<B>,
    third: Observable<C>,
    fourth: Observable<D>,
    fifth: Observable<E>
): Observable<Quintuple<A, B, C, D, E>> =
    Observable.combineLatest(listOf(first, second, third, fourth, fifth)) { args ->
        Quintuple(args[0] as A, args[1] as B, args[2] as C, args[3] as D, args[4] as E)
    }
