package by.varyvoda.lab2.domain.methods

import by.varyvoda.lab2.domain.Generator
import by.varyvoda.lab2.domain.input.NumberInput

class Simpson : Method() {

    private val a = NumberInput("a", 2, maxM - 1)
    private val r = NumberInput("r", 1, maxM - 1)
    private val m = NumberInput("m", 2, maxM)

    override val specificParameters = listOf(a, r, m)

    override fun name(): String {
        return "Simpson"
    }

    override fun generator(): Generator {
        return SimpsonGenerator(
            bestLemerGenerator(),
            Lemer.LemerGenerator(a.value, r.value, m.value)
        )
    }

    class SimpsonGenerator(private val first: Generator, private val second: Generator) : Generator {

        override fun next(): Double {
            return first.next() / 2 + second.next() / 2
        }

        override fun reset() {
            first.reset()
            second.reset()
        }

        override fun math(): Double {
            return 0.0
        }

        override fun dispersion(): Double {
            return 0.0
        }
    }
}