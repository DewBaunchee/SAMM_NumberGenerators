package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.calculateIntervals
import by.varyvoda.numbergenerators.domain.calculatePairCheck
import by.varyvoda.numbergenerators.domain.calculatePeriods
import by.varyvoda.numbergenerators.domain.input.NumberInput
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt

abstract class Method {

    abstract fun name(): String

    abstract fun generator(): Generator

    protected abstract val specificParameters: List<List<NumberInput>>

    fun summaryStatistics(): SummaryStatistics {
        val n = 1000
        val generator = generator()
        val values = generator.batch(n)
        val math = values.sum() / n
        val dispersion = values.reduce { acc, d -> acc + (d - math).pow(2) } * 1 / n
        val standardDeviation = sqrt(dispersion)

        val intervals = calculateIntervals(values)

        generator.reset()
        val periods = calculatePeriods(generator)

        generator.reset()
        val pairCheck = calculatePairCheck(generator)

        return SummaryStatistics(
            intervals,
            math to generator.math(),
            dispersion to generator.dispersion(),
            standardDeviation to generator.standardDeviation(),
            periods.first,
            periods.second,
            pairCheck
        )
    }

    fun parameters(): List<List<NumberInput>> {
        return specificParameters
    }

    fun random() {
        parameters().forEach { line -> line.forEach { it.random() } }
    }

    abstract fun best()

    data class SummaryStatistics(
        val intervals: List<Interval>,
        val math: Pair<Double, Double>,
        val dispersion: Pair<Double, Double>,
        val standardDeviation: Pair<Double, Double>,
        val p: Int?,
        val l: Int?,
        val pairCheck: Double
    )

    data class Interval(val frequency: Double, val values: List<Double>) {

        companion object {

            private val toFixed2 = DecimalFormat("#.#####")

        }

        val range: String get() = "${toFixed2.format(values.first())} - ${toFixed2.format(values.last())}"
    }
}