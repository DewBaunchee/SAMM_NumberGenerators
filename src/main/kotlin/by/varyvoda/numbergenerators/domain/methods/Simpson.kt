package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.input.NumberInput
import kotlin.math.max
import kotlin.math.min

class Simpson : Method() {

    private val equality = Equality()

    override val specificParameters: List<List<NumberInput>> = listOf(listOf(equality.a, equality.b))

    override fun name(): String {
        return "Simpson"
    }

    override fun generator(): Generator {
        val a = min(equality.a.value.toDouble(), equality.b.value.toDouble())
        val b = max(equality.a.value.toDouble(), equality.b.value.toDouble())
        return SimpsonGenerator(Equality.EqualityGenerator(bestLemerGenerator(), a / 2, b / 2))
    }

    override fun best() {
        equality.best()
    }

    class SimpsonGenerator(private val generator: Generator) : Generator {

        override fun next(): Double {
            return generator.next() + generator.next()
        }

        override fun reset() {
            generator.reset()
        }

        override fun math(): Double {
            return 0.0
        }

        override fun dispersion(): Double {
            return 0.0
        }
    }
}