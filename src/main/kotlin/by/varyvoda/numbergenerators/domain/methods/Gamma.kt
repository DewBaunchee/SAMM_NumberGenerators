package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.input.DoubleInput
import by.varyvoda.numbergenerators.domain.input.NumberInput
import by.varyvoda.numbergenerators.domain.mul
import kotlin.math.ln
import kotlin.math.pow

class Gamma : Method() {

    private val eta = DoubleInput("η", 0.000001, Double.MAX_VALUE)
    private val lambda = DoubleInput("λ", 0.000001, Double.MAX_VALUE)

    override val specificParameters: List<List<NumberInput>> = listOf(listOf(eta, lambda))

    override fun name(): String {
        return "Gamma"
    }

    override fun generator(): Generator {
        return GammaGenerator(eta.value.toDouble(), lambda.value.toDouble())
    }

    override fun best() {
        eta.value = 5.0
        lambda.value = 8506.0
    }

    class GammaGenerator(private val eta: Double, private val lambda: Double) : Generator {

        private val lemer = bestLemerGenerator()

        override fun next(): Double {
            return (-1.0 / lambda) * ln(lemer.batch(eta.toInt()).mul())
        }

        override fun reset() {
            lemer.reset()
        }

        override fun math(): Double {
            return eta / lambda
        }

        override fun dispersion(): Double {
            return eta / lambda.pow(2)
        }
    }
}