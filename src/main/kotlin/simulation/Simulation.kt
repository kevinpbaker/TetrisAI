package simulation

import GameBoard
import GamePiece
import game.Game
import game.Shape
import game.ShapeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import neural.Individual
import neural.Move
import SIMULATION_SCORE_CAP
import java.util.*

class Simulation(
    val i: Individual
): Game {

    companion object {
        const val rows = 20
        const val cols = 10
    }

    override val shapes: MutableList<Shape> = mutableListOf(
        Shape(4, intArrayOf(8, 9, 10, 11), 255, ShapeType.I),          // I
        Shape(3, intArrayOf(0, 3, 4, 5),   255, ShapeType.RIGHT_GUN),  // J
        Shape(3, intArrayOf(2, 3, 4, 5),   255, ShapeType.LEFT_GUN),   // L
        Shape(2, intArrayOf(0, 1, 2, 3),   255, ShapeType.SQUARE),     // O
        Shape(4, intArrayOf(5, 6, 8, 9),   255, ShapeType.LEFT_SNAKE), // S
        Shape(3, intArrayOf(1, 3, 4, 5),   255, ShapeType.T),          // T
        Shape(4, intArrayOf(4, 5, 9, 10),  255, ShapeType.RIGHT_SNAKE) // Z
    )
    override val shapeQueue: Stack<Shape> = Stack()
    override var movesInQueue: Queue<Move> = LinkedList()
    override var next: Shape? = null
    override fun getCurrentPiece(): GamePiece? = curr
    override fun getBoard(): GameBoard = this.grid
    override fun getNextPiece(): GamePiece? {
        next?.let { return Setromino(grid, it) }
        return null
    }

    var playing = true
    var placed: Array<IntArray> = run {
        val a = Array(cols) { IntArray(rows) }
        for (col in 0 until cols) for (row in 0 until rows) a[col][row] = 0
        a
    }
    var grid = Srid(20,10, placed)
    var curr: Setromino? = null
    var score = 0.0
    var lines = 0
    var timeAlive = 0
    var level = 1

    init {
        setup()
    }

    private fun setup() {
        playing = true
        loadNextShape()
        loadNext()
    }

    suspend fun simulate(): Individual = withContext(Dispatchers.Default) {
        while (playing) { play() }
        return@withContext i
    }

    private suspend fun play() {
        i.calculateBestMove(this)?.second?.let { movesInQueue = it }
        simulateMove()
        updateBoard()
        updateIndividual()
        loadNext()
    }

    private fun simulateMove() {
        while (movesInQueue.isNotEmpty()) {
            when (movesInQueue.poll()) {
                Move.LEFT -> curr?.left(grid)
                Move.RIGHT -> curr?.right(grid)
                Move.ROTATE -> curr?.rotate(grid)
                Move.HARD_DOWN -> score += curr?.hardDown() ?: 0.0
                else -> {}
            }
            timeAlive++
        }
    }

    private fun updateBoard() {
        lines += this.grid.howManyLinesCleared()
        score += this.grid.clearLinesAndReturnScoreToAdd()
        level = 1 + lines/10
    }

    private fun updateIndividual() {
        i.lines = lines
        i.score = score
        i.level = level
        i.timeAlive = timeAlive
    }

    private fun loadNext() {
        next?.let { curr = Setromino(grid, it) }
        loadNextShape()
        val current = this.curr
        playing = if (current != null) {
            val legalMove = this.curr?.isValidPosition(grid, current.shape.matrix, 3, -1) ?: false
            val hugeScore = score > SIMULATION_SCORE_CAP
            legalMove && !hugeScore
        } else {
            false
        }
    }

}