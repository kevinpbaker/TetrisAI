package neural

import koma.extensions.forEach
import koma.extensions.forEachIndexed
import koma.extensions.get
import koma.extensions.set
import koma.fill
import koma.matrix.Matrix
import koma.max
import koma.randn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class BigBrain(
    var iNodes: Int,
    var hNodes: Int,
    var oNodes: Int,
    var hLayers: Int
): Brain {

    // Plus 1 for bias
    var iWeights: Matrix<Double> = fill(hNodes, iNodes + 1) { r, col ->
        Random.nextDouble(Brain.INIT_FROM, Brain.INIT_UNTIL)
    }
    var hWeights: List<Matrix<Double>> = run {
        val hiddenW = mutableListOf<Matrix<Double>>()
        repeat(hLayers) { hiddenW.add(fill(hNodes, hNodes + 1) { _, _ ->
            Random.nextDouble(Brain.INIT_FROM, Brain.INIT_UNTIL) })
        }
        hiddenW
    }
    var oWeights: Matrix<Double> = fill(oNodes, hNodes + 1) { _, _ ->
        Random.nextDouble(Brain.INIT_FROM, Brain.INIT_UNTIL)
    }

    override suspend fun mutate() = withContext(Dispatchers.Default) {
        launch { iWeights.mutate(Brain.MUTATION_RATE) }
        launch { hWeights.forEach { it.mutate(Brain.MUTATION_RATE) } }
        launch { oWeights.mutate(Brain.MUTATION_RATE) }
        joinAll()
    }

    override fun deepCopy(): BigBrain {
        val x = BigBrain(iNodes, hNodes, oNodes, hLayers)
        x.setWeights(iWeights.copy(), hWeights.map { it.copy() }, oWeights.copy())
        return x
    }

    fun setWeights(iW: Matrix<Double>, hW: List<Matrix<Double>>, oW: Matrix<Double>) {
        this.iWeights = iW
        this.hWeights = hW
        this.oWeights = oW
    }

    override fun output(input: Matrix<Double>): Double {
        // Input
        var curr_bias = input.addBias()
        var hidden_ip = iWeights * curr_bias
        var hidden_op = hidden_ip.activate()
        curr_bias = hidden_op.addBias()
        // Hidden
        hWeights.forEach {
            hidden_ip = it * curr_bias
            hidden_op = hidden_ip.activate()
            curr_bias = hidden_op.addBias()
        }
        // Output
        return (oWeights * curr_bias)[0]
    }

    override fun crossover(partner: Brain): Brain {
        return if (partner is BigBrain) {
            val child = BigBrain(iNodes, hNodes, oNodes, hLayers)
            child.iWeights = child.iWeights.crossover(partner.iWeights)
            child.hWeights = child.hWeights.mapIndexed { i, me -> me.crossover(partner.hWeights[i]) }
            child.oWeights = child.oWeights.crossover(partner.oWeights)
            child
        } else {
            this
        }
    }

    override fun getWeights(): String {
        return this.iWeights.printMe()
    }

    fun clone(): BigBrain {
        return deepCopy()
    }
}

fun Matrix<Double>.printMe(): String {
    var out = ""
    this.forEach { out += "$it " }
    return out
}

fun Matrix<Double>.crossover(partner: Matrix<Double>): Matrix<Double> {
    val randC: Int = Random.nextInt(this.numCols())
    val randR: Int = Random.nextInt(this.numRows())
    return fill(this.numRows(), this.numCols()) { row, col ->
        if(row <= randR && col <= randC) {
            this[row,col]
        } else {
            partner[row,col]
        }
    }
}

fun Matrix<Double>.mutate(mutationRate: Double) {
    this.forEachIndexed { row, col, _ ->
        if (Random.nextDouble(1.0) < mutationRate) {
            this[row, col] += ((randn(1, 1)[0]/5) * listOf(-1,1).random())
            if (this[row, col] > 1) this[row, col] = 1
            if (this[row, col] < -1) this[row, col] = -1
        }
    }
}

/**
 * Flattens a matrix and adds 1 to bottom
 */
fun Matrix<Double>.addBias(): Matrix<Double> {
    val flat = this.getDoubleData()
    return fill((this.numRows() * numCols()) + 1, 1) { row, _ ->
        if (row < flat.size) flat[row] else 1.0
    }
}

fun Matrix<Double>.activate(): Matrix<Double> {
    return fill(this.numRows(), this.numCols()) { r, c -> relu(this[r, c]) }
}

fun relu(x: Double): Double { return max(0.0, x) }