package by.varyvoda.lab2.domain.methods

import by.varyvoda.lab2.domain.Generator
import by.varyvoda.lab2.domain.input.NumberInput
import kotlin.math.log10
import kotlin.math.pow

class Exponential : Method() {

    private val lambda = NumberInput("λ", 1, 1000)

    override val specificParameters = listOf(lambda)

    override fun name(): String {
        return "Exponential"
    }

    override fun generator(): Generator {
        return ExponentialGenerator(lambda.value)
    }

    class ExponentialGenerator(private val lambda: Int) : Generator {

        private val lemer = bestLemerGenerator()

        override fun next(): Double {
            return (-1.0 / lambda) * log10(lemer.next())
        }

        override fun reset() {
            lemer.reset()
        }

        override fun math(): Double {
            return 1.0 / lambda
        }

        override fun dispersion(): Double {
            return 1.0 / lambda.toDouble().pow(2)
        }

        override fun standardDeviation(): Double {
            return math()
        }
    }
}