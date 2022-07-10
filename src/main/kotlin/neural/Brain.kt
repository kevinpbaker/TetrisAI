package neural

import koma.matrix.Matrix

interface Brain {
    companion object {
        const val MUTATION_RATE: Double = 0.15
        const val INIT_FROM = -1.0
        const val INIT_UNTIL = 1.0
    }
    fun output(input: Matrix<Double>): Double
    fun deepCopy(): Brain
    suspend fun mutate()
    fun crossover(partner: Brain): Brain
    fun getWeights(): String
}