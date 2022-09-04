package by.varyvoda.lab2.domain.methods

import by.varyvoda.lab2.domain.Generator
import by.varyvoda.lab2.domain.input.NumberInput
import by.varyvoda.lab2.domain.mul
import kotlin.math.log10
import kotlin.math.pow

class Gamma : Method() {

    private val eta = NumberInput("η", 1, 10)
    private val lambda = NumberInput("λ", 1, 10000)

    override val specificParameters: List<NumberInput> = listOf(eta, lambda)

    override fun name(): String {
        return "Gamma"
    }

    override fun generator(): Generator {
        return GammaGenerator(eta.value, lambda.value)
    }

    class GammaGenerator(private val eta: Int, private val lambda: Int) : Generator {

        private val lemer = bestLemerGenerator()

        override fun next(): Double {
            return (-1.0 / lambda) * log10(lemer.batch(eta).mul())
        }

        override fun reset() {
            lemer.reset()
        }

        override fun math(): Double {
            return eta.toDouble() / lambda
        }

        override fun dispersion(): Double {
            return eta.toDouble() / lambda.toDouble().pow(2)
        }
    }
}