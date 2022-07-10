package neural

import koma.extensions.get
import koma.fill
import koma.matrix.Matrix
import kotlin.random.Random

class SimpleBrain(
    var iNodes: Int
): Brain {

    var iWeights: Matrix<Double> = fill(1, iNodes) { r, col ->
        when (col) {
            0 -> -0.510066
            1 -> 0.760666
            2 -> -0.35663
            3 -> -0.184483
            else -> Random.nextDouble(Brain.INIT_FROM, Brain.INIT_UNTIL)
        }
    }

    override fun output(input: Matrix<Double>): Double {
        return (iWeights * input)[0]
    }

    override fun deepCopy(): Brain {
        val x = SimpleBrain(iNodes)
        x.iWeights = iWeights.copy()
        return x
    }

    override suspend fun mutate() {
        iWeights.mutate(Brain.MUTATION_RATE)
    }

    override fun crossover(partner: Brain): Brain {
        return if (partner is SimpleBrain) {
            val child = SimpleBrain(iNodes)
            child.iWeights = child.iWeights.crossover(partner.iWeights)
            child
        } else {
            this
        }
    }

    override fun getWeights(): String {
        return this.iWeights.printMe()
    }
}