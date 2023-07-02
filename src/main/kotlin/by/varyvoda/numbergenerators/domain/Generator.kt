package by.varyvoda.numbergenerators.domain

import java.util.stream.IntStream
import kotlin.math.sqrt
import kotlin.streams.toList

interface Generator {

    fun next(): Double

    fun reset()

    fun math(): Double

    fun dispersion(): Double

    fun standardDeviation(): Double {
        return sqrt(dispersion())
    }

    fun batch(n: Int): List<Double> {
        return IntStream.range(0, n).mapToObj { next() }.toList()
    }

    fun pair(): Pair<Double, Double> {
        return next() to next()
    }
}