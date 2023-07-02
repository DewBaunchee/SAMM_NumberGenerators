package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.input.DoubleInput
import by.varyvoda.numbergenerators.domain.input.NumberInput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class Equality : Method() {

    val a = DoubleInput("a", Double.MIN_VALUE, Double.MAX_VALUE)

    val b = DoubleInput("b", Double.MIN_VALUE, Double.MAX_VALUE)

    override val specificParameters: List<List<NumberInput>> = listOf(listOf(a, b))

    override fun name(): String {
        return "Equal"
    }

    override fun best() {

    }

    override fun generator(): Generator {
        val a = min(a.value.toDouble(), b.value.toDouble())
        val b = max(this.a.value.toDouble(), b.value.toDouble())
        return EqualityGenerator(bestLemerGenerator(), a, b)
    }

    class EqualityGenerator(private val generator: Generator, private val a: Double, private val b: Double) :
        Generator {

        override fun next(): Double {
            return generator.next() * (b - a) + a
        }

        override fun reset() {
            generator.reset()
        }

        override fun math(): Double {
            return (a + b) / 2.0
        }

        override fun dispersion(): Double {
            return (b - a).pow(2) / 12.0
        }
    }
}