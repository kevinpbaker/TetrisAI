import game.Tetris
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import neural.Individual
import neural.Population
import simulation.Simulation

val default = CoroutineScope(Dispatchers.Default)
const val POP_SIZE = 50
const val NUMBER_OF_TETRIS_WINDOWS = 3
const val SIMULATION_SCORE_CAP = 2000000
const val START_PLAYING_WHEN_AI_CAN_SCORE = 1000000.0
const val tetrisGameWidth = 400
var pop: Population = Population(POP_SIZE)
var tetris: List<Tetris?> = emptyList()
val simulatedTetris = mutableListOf<Deferred<Any>>()
var training = true
var playing = true

suspend fun main() {
    startTetrisEnvironment()
    createTetrisGod()
    letTheTetrisGodPlay()
}

suspend fun startTetrisEnvironment() {
    tetris = pop.pop.take(NUMBER_OF_TETRIS_WINDOWS).map { default.async { Tetris.run(it) } }.awaitAll()
    pop.pop.mapIndexed { index, individual ->
        if (index !in 0 until NUMBER_OF_TETRIS_WINDOWS) {
            val simulation = default.async { Simulation(individual).simulate() }
            simulatedTetris.add(simulation)
        }
    }
    awaitSimulationsAndThenMutate()
}

suspend fun createTetrisGod() {
    while (training) {
        val best = pop.globalBestIndividual
        if (best != null && best.fitness >= START_PLAYING_WHEN_AI_CAN_SCORE) {
            training = false
        } else if (best != null) {
            breedTetrisGod()
        }
    }
}

suspend fun breedTetrisGod() {
    val p = pop.pop
    simulatedTetris.clear()
    // Don't mutate the best AI
    p.mapIndexed { index, individual -> if (index != 0) addToSimulationQueue(individual) }
    awaitSimulationsAndThenMutate()
}

suspend fun addToSimulationQueue(individual: Individual) {
    val simulation = default.async { Simulation(individual).simulate() }
    simulatedTetris.add(simulation)
}

suspend fun awaitSimulationsAndThenMutate() {
    simulatedTetris.awaitAll()
    println("Gen: ${pop.gen}. Mutating...")
    pop.naturalSelection()
}

suspend fun letTheTetrisGodPlay() {
    val best = pop.globalBestIndividual
    if (best != null) {
        val players = (0 until NUMBER_OF_TETRIS_WINDOWS).map{ flowForever(it, best) }.toTypedArray()
        merge(*players).collect()
    }
    while(playing) delay(1000000)
}

suspend fun flowForever(pos: Int, i: Individual): Flow<Boolean?> = flow {
    @Suppress("RedundantAsync")
    while (true) emit(default.async { tetris[pos]?.runAgain(i) }.await())
}