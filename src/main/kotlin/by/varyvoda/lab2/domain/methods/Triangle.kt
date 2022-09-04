package by.varyvoda.lab2.domain.methods

import by.varyvoda.lab2.domain.Generator
import by.varyvoda.lab2.domain.input.NumberInput
import kotlin.math.max
import kotlin.math.min

class Triangle : Method() {

    private val a = NumberInput("a", 0, 1000)
    private val b = NumberInput("b", 0, 1000)

    override val specificParameters: List<NumberInput> = listOf(a, b)

    override fun name(): String {
        return "Triangle"
    }

    override fun generator(): Generator {
        return TriangleGenerator(a.value, b.value)
    }

    class TriangleGenerator(private val a: Int, private val b: Int) : Generator {

        private val lemer = bestLemerGenerator()

        override fun next(): Double {
            return if (lemer.next() < 0.5)
                a + (b - a) * max(lemer.next(), lemer.next())
            else
                a + (b - a) * min(lemer.next(), lemer.next())
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