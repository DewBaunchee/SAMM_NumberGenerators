package by.varyvoda.lab2.domain.methods

import by.varyvoda.lab2.domain.Generator
import by.varyvoda.lab2.domain.input.NumberInput
import kotlin.math.pow
import kotlin.math.sqrt

class Gaussian : Method() {

    private val n = NumberInput("n", 6, 12)
    private val mx = NumberInput("mx", 0, 100000)
    private val sigma = NumberInput("σx", 0, 100000)

    override val specificParameters: List<NumberInput> = listOf(n, mx, sigma)

    override fun name(): String {
        return "Gaussian"
    }

    override fun generator(): Generator {
        return GaussianGenerator(n.value, mx.value, sigma.value)
    }

    class GaussianGenerator(private val n: Int, private val mx: Int, private val sigma: Int) : Generator {

        private val lemer = bestLemerGenerator()

        override fun next(): Double {
            return mx + sigma * sqrt(12.0 / n) * (lemer.batch(n).sum() - n.toDouble() / 2)
        }

        override fun reset() {
            lemer.reset()
        }

        override fun math(): Double {
            return mx.toDouble()
        }

        override fun dispersion(): Double {
            return sigma.toDouble().pow(2)
        }

        override fun standardDeviation(): Double {
            return sigma.toDouble()
        }
    }
}