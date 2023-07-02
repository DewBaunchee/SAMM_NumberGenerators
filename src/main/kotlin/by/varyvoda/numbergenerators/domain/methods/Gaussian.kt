package by.varyvoda.numbergenerators.domain.methods

import by.varyvoda.numbergenerators.domain.Generator
import by.varyvoda.numbergenerators.domain.input.DoubleInput
import by.varyvoda.numbergenerators.domain.input.IntegerInput
import by.varyvoda.numbergenerators.domain.input.NumberInput
import kotlin.math.pow
import kotlin.math.sqrt

class Gaussian : Method() {

    private val n = IntegerInput("n", 6, 12)
    private val mx = DoubleInput("mx", Double.MIN_VALUE, Double.MAX_VALUE)
    private val sigma = DoubleInput("σx", Double.MIN_VALUE, Double.MAX_VALUE)

    override val specificParameters: List<List<NumberInput>> = listOf(listOf(n, mx, sigma))

    override fun name(): String {
        return "Gaussian"
    }

    override fun generator(): Generator {
        return GaussianGenerator(n.value.toInt(), mx.value.toDouble(), sigma.value.toDouble())
    }

    override fun best() {
        n.value = 9.0
        mx.value = 18734.0
        sigma.value = 62551.0
    }

    class GaussianGenerator(private val n: Int, private val mx: Double, private val sigma: Double) : Generator {

        private val lemer = bestLemerGenerator()

        override fun next(): Double {
            return mx + sigma * sqrt(12.0 / n) * (lemer.batch(n).sum() - n.toDouble() / 2)
        }

        override fun reset() {
            lemer.reset()
        }

        override fun math(): Double {
            return mx
        }

        override fun dispersion(): Double {
            return sigma.pow(2)
        }

        override fun standardDeviation(): Double {
            return sigma
        }
    }
}