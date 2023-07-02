package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.input.DoubleInput
import by.varyvoda.numbergenerators.domain.input.NumberInput
import kotlin.math.ln
import kotlin.math.pow

class Exponential : Method() {

    private val lambda = DoubleInput("λ", 0.00001, Double.MAX_VALUE)

    override val specificParameters: List<List<NumberInput>> = listOf(listOf(lambda))

    override fun name(): String {
        return "Exponential"
    }

    override fun generator(): Generator {
        return ExponentialGenerator(lambda.value.toDouble())
    }

    override fun best() {
        lambda.value = 598.0
    }

    class ExponentialGenerator(private val lambda: Double) : Generator {

        private val lemer = bestLemerGenerator()

        override fun next(): Double {
            return (-1.0 / lambda) * ln(lemer.next())
        }

        override fun reset() {
            lemer.reset()
        }

        override fun math(): Double {
            return 1.0 / lambda
        }

        override fun dispersion(): Double {
            return 1.0 / lambda.pow(2)
        }

        override fun standardDeviation(): Double {
            return math()
        }
    }
}