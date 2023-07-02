package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.input.IntegerInput
import by.varyvoda.numbergenerators.domain.input.NumberInput
import kotlin.math.pow

const val n = 16

val maxM = 2.0.pow(n.toDouble()).toInt()

fun bestLemerGenerator(): Lemer.LemerGenerator {
    return Lemer.LemerGenerator(3975.0, 44800.0, 10060.0)
}

class Lemer : Method() {

    val a = IntegerInput("a", 2, maxM - 1)
    val r = IntegerInput("r", 1, maxM - 1)
    val m = IntegerInput("m", 2, maxM)

    override val specificParameters: List<List<NumberInput>> = listOf(listOf(a, r, m))

    override fun name(): String {
        return "Lemer"
    }

    override fun generator(): Generator {
        return LemerGenerator(a.value.toDouble(), r.value.toDouble(), m.value.toDouble())
    }

    override fun best() {
        a.value = 3975
        r.value = 44800
        m.value = 10060
    }

    class LemerGenerator(private val a: Double, private val r0: Double, private val m: Double) : Generator {

        private var previous: Double = r0

        override fun next(): Double {
            previous = (a * previous) % m
            return previous / m
        }

        override fun reset() {
            previous = r0
        }

        override fun math(): Double {
            return (0.0 + 1.0) / 2
        }

        override fun dispersion(): Double {
            return (1.0 - 0.0).pow(2) / 12.0
        }
    }
}