package by.varyvoda.lab2.domain.methods

import by.varyvoda.lab2.domain.Generator
import by.varyvoda.lab2.domain.input.NumberInput
import kotlin.math.pow

const val n = 16

val maxM = 2.0.pow(n.toDouble()).toInt()

fun bestLemerGenerator(): Lemer.LemerGenerator {
    return Lemer.LemerGenerator(3975, 44800, 10060)
}

class Lemer : Method() {

    val a = NumberInput("a", 2, maxM - 1)
    val r = NumberInput("r", 1, maxM - 1)
    val m = NumberInput("m", 2, maxM)

    override val specificParameters = listOf(a, r, m)

    override fun name(): String {
        return "Lemer"
    }

    override fun generator(): Generator {
        return LemerGenerator(a.value, r.value, m.value)
    }

    class LemerGenerator(val a: Int, val r0: Int, val m: Int) : Generator {

        private var previous: Int = r0

        override fun next(): Double {
            previous = (a * previous) % m
            return previous.toDouble() / m
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