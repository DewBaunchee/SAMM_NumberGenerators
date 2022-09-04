package by.varyvoda.lab2.domain.methods

import by.varyvoda.lab2.domain.Generator
import by.varyvoda.lab2.domain.calculateIntervals
import by.varyvoda.lab2.domain.calculatePeriods
import by.varyvoda.lab2.domain.input.NumberInput
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt

abstract class Method {

    abstract fun name(): String

    abstract fun generator(): Generator

    protected abstract val specificParameters: List<NumberInput>

    fun summaryStatistics(): SummaryStatistics {
        val n = 1000
        val generator = generator()
        val values = generator.batch(n)
        val math = values.sum() * 1 / n
        val dispersion = values.reduce { acc, d -> acc + (d - math).pow(2) } * 1 / n
        val standardDeviation = sqrt(dispersion)

        val intervals = calculateIntervals(values)

        generator.reset()
        val periods = calculatePeriods(generator)
        return SummaryStatistics(
            intervals,
            math to generator.math(),
            dispersion to generator.dispersion(),
            standardDeviation to generator.standardDeviation(),
            periods.first,
            periods.second
        )
    }

    fun parameters(): List<NumberInput> {
        return specificParameters
    }

    fun random() {
        parameters().forEach { it.random() }
    }

    data class SummaryStatistics(
        val intervals: List<Interval>,
        val math: Pair<Double, Double>,
        val dispersion: Pair<Double, Double>,
        val standardDeviation: Pair<Double, Double>,
        val p: Int?,
        val l: Int?
    )

    data class Interval(val frequency: Double, val values: List<Double>) {

        companion object {

            private val toFixed2 = DecimalFormat("#.#####")

        }

        val range: String get() = "${toFixed2.format(values.first())} - ${toFixed2.format(values.last())}"
    }
}