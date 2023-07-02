package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.input.DoubleInput
import by.varyvoda.numbergenerators.domain.input.NumberInput
import kotlin.math.max

class Triangle : Method() {

    private val lemerMethod = Lemer()

    private val a = DoubleInput("a", Double.MIN_VALUE, Double.MAX_VALUE)
    private val b = DoubleInput("b", Double.MIN_VALUE, Double.MAX_VALUE)

    override val specificParameters: List<List<NumberInput>> = lemerMethod.parameters().plus(listOf(listOf(a, b)))

    override fun name(): String {
        return "Triangle"
    }

    override fun generator(): Generator {
        return TriangleGenerator(lemerMethod.generator(), a.value.toDouble(), b.value.toDouble())
    }

    override fun best() {
        lemerMethod.a.value = 38896
        lemerMethod.r.value = 17510
        lemerMethod.m.value = 55405
        a.value = 273
        b.value = 474
    }

    class TriangleGenerator(private val lemer: Generator, private val a: Double, private val b: Double) : Generator {

        override fun next(): Double {
            return a + (b - a) * max(lemer.next(), lemer.next())
        }

        override fun reset() {
            lemer.reset()
        }

        override fun math(): Double {
            return 0.0
        }

        override fun dispersion(): Double {
            return 0.0
        }
    }
}