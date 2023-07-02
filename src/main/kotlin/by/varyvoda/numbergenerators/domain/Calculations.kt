package by.varyvoda.numbergenerators.domain

import by.varyvoda.numbergenerators.domain.methods.Method
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.math.pow

fun calculateIntervals(values: List<Double>): List<Method.Interval> {
    val minMax = values.stream().mapToDouble { it }.summaryStatistics()
    val varietyRange = minMax.max - minMax.min
    val intervalCount = 40
    val intervalLength = varietyRange / intervalCount
    if (intervalLength == 0.0) return emptyList()

    val sortedValues = values.sortedBy { it }

    var intervalStart = sortedValues.first()
    var currentInterval = mutableListOf<Double>()
    val intervals = mutableListOf<List<Double>>()
    sortedValues.forEach {
        if (it < intervalStart + intervalLength) {
            currentInterval.add(it)
        } else {
            intervals.add(currentInterval)
            currentInterval = mutableListOf(it)
            intervalStart = it
        }
    }
    intervals.add(currentInterval)

    return intervals
        .filter { it.isNotEmpty() }
        .map { Method.Interval(it.size.toDouble(), it) }
        .toList()
}

fun calculatePeriods(generator: Generator): Pair<Int?, Int?> {
    val v = 1_000_000
    IntStream.range(0, v).forEach { generator.next() }
    val xV = generator.next()

    generator.reset()
    var i1: Int? = null
    var i2: Int? = null
    Stream.iterate(0) { it + 1 }
        .takeWhile { i1 == null || i2 == null }
        .forEach {
            if (xV == generator.next()) {
                if (i1 == null) i1 = it
                else i2 = it
            }
        }
    val p = if (i2 == null || i1 == null) null else i2!! - i1!!

    generator.reset()
    val cache = mutableListOf<Double>()
    var i3: Int? = null
    if (p != null) {
        IntStream.range(0, v)
            .takeWhile { i3 == null }
            .peek { cache.add(generator.next()) }
            .skip(p.toLong())
            .forEach {
                if (cache[it - p] == generator.next()) {
                    i3 = it - p
                }
            }
    }
    return p to if (i3 == null || p == null) null else (i3!! + p)
}

fun calculatePairCheck(generator: Generator): Double {
    val n = 1000
    val k = IntStream.range(0, n)
        .mapToObj { generator.pair() }
        .filter { it.first.pow(2) + it.second.pow(2) < 1 }
        .count()
    return k.toDouble() / n
}

fun Iterable<Double>.mul(): Double {
    var mul = 1.0
    for (element in this) {
        mul *= element
    }
    return mul
}