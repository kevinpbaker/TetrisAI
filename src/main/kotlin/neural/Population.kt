package neural

import default
import kotlinx.coroutines.*
import kotlin.random.Random


class Population(
    size: Int
) {

    var gen = 1
    var globalBest = 0.0
    var currentBest = 0.0
    var pop = mutableListOf<Individual>()
    var globalBestIndividual: Individual? = null
    var totalFitness = 0.0

    init {
        for (x in 0 until size) pop.add(Individual())
        default.launch {
            globalBestIndividual = pop[0].clone()
            joinAll()
        }
    }

    suspend fun setBest() {
        // Sort based on fitness
        pop.sortByDescending { it.fitness }
        totalFitness = pop.sumOf { it.fitness }
        currentBest = pop[0].fitness
        if (currentBest > globalBest) {
            globalBestIndividual = pop[0].clone()
            globalBest = pop[0].fitness
        }
        println("Current best ${currentBest}: ${pop[0].lines}, Global best ${globalBest}: ${globalBestIndividual?.lines}")
        saveBestBrain(pop[0])
    }

    // TODO
    fun saveBestBrain(i: Individual) {
    }

    suspend fun naturalSelection() {
        println("Natural selection...")
        setBest()
        val newPop = mutableListOf<Individual>()
        globalBestIndividual?.let { newPop.add(it.clone()); }  // Add best to new
        newPop.add(pop[0].clone())
        repeat(5) {
            val child = pop[0].crossover(selectParent())
            child.mutate()
            newPop.add(child)
        }
        repeat(pop.size-7) {
            val child = selectParent().crossover(selectParent())
            child.mutate()
            newPop.add(child)
        }
        pop = newPop
        gen += 1
        resetKnowledgeOfGame()
    }

    /**
     * selects a random number in range of the fitness sum and if a brain falls in that range then select it
     */
    private fun selectParent(): Individual {
        val rand = Random.nextDouble(totalFitness)
        var summation = 0.0
        for (i in 0 until pop.size) {
            summation += pop[i].fitness
            if (summation > rand) {
                return pop[i]
            }
        }
        return pop[0]
    }

    private fun resetKnowledgeOfGame() {
        pop.forEach { it.resetKnowledgeOfGame() }
    }
}
