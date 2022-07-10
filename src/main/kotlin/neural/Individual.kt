package neural

import GameBoard
import GamePiece
import game.Game
import game.ShapeType
import default
import kotlinx.coroutines.*
import simulation.Setromino
import simulation.Srid
import java.io.Serializable
import java.util.*


class Individual: Serializable {

    var brain: Brain = BigBrain(4, 4, 1, 1) // SimpleBrain(4)
    var score: Double = 0.0
    var level: Int = 1
    var lines: Int = 0
    val fitness: Double
        get() { return score }
    var timeAlive: Int = 0
    var data: MutableList<Int> = mutableListOf()

    fun resetKnowledgeOfGame() {
        score = 0.0
        level = 1
        lines = 0
        timeAlive = 0
    }

    suspend fun clone(): Individual = withContext(Dispatchers.Default) {
        val a = Individual()
        copyBasicDate(a)
        a.brain = brain.deepCopy()
        return@withContext a
    }

    fun copyBasicDate(a: Individual) {
        a.score = score
        a.level = level
        a.lines = lines
        a.timeAlive = timeAlive
        a.data = data.toList().toMutableList()
    }

    fun crossover(parent: Individual): Individual {
        val child = Individual()
        child.brain = brain.crossover(parent.brain)
        return child
    }

    suspend fun mutate() {
        brain.mutate()
    }

    suspend fun calculateBestMove(game: Game): Pair<Double, Queue<Move>>? = withContext(Dispatchers.Unconfined) {
        val board = game.getBoard() ?: return@withContext null
        val piece = game.getCurrentPiece() ?: return@withContext null
        val nextPiece = game.getNextPiece()
        setBestMoveForPiece(board, piece, nextPiece)
    }

    private suspend fun setBestMoveForPiece(
        board: GameBoard,
        piece: GamePiece,
        nextPiece: GamePiece?,
    ): Pair<Double, Queue<Move>>? {
        val calculateBestMove = mutableListOf<Deferred<Pair<Double, Queue<Move>>>>()
        val simBoard = when (piece.shape.type) {
            ShapeType.I -> Srid(board.rows, board.cols, getCopyOfColors(board))
            else -> Srid(board.rows, board.cols, getCopyOfColors(board))
        }
        val simPiece = Setromino(simBoard, piece.shape)
        val nRotations = when (simPiece.shape.type) {
            ShapeType.SQUARE -> 1
            ShapeType.I -> 2
            else -> 4
        }
        for (rotate in 0 until nRotations) {
            if (rotate != 0) simPiece.rotate(board)
            // Middle
            calculateBestMove.add(default.async {
                simulateMove(simBoard, piece, nextPiece, 0, 0, rotate)
            })
            for (left in 1..(simPiece.x+1)) {
                calculateBestMove.add(default.async {
                    simulateMove(simBoard, piece, nextPiece, left, 0, rotate)
                })
            }
            for (right in 1..((simBoard.cols-1)-(simPiece.x))) {
                calculateBestMove.add(default.async {
                    simulateMove(simBoard, piece, nextPiece, 0, right, rotate)
                })
            }
        }
        val scoreAndMoveSet = calculateBestMove.map { it.await() }
        val bestMove = scoreAndMoveSet.maxByOrNull { it.first }
        return bestMove
    }

    private suspend fun simulateMove(
        board: GameBoard,
        piece: GamePiece,
        nextPiece: GamePiece?,
        moveLeft: Int,
        moveRight: Int,
        rotate: Int
    ): Pair<Double, Queue<Move>> = withContext(Dispatchers.Unconfined) {
        val srid = Srid(board.rows, board.cols, getCopyOfColors(board))
        val copyOfPiece = Setromino(srid, piece.shape)
        val moveList = LinkedList<Move>() as Queue<Move>
        repeat(rotate) { copyOfPiece.rotate(board); moveList.add(Move.ROTATE) }
        repeat(moveLeft) { copyOfPiece.left(board); moveList.add(Move.LEFT) }
        repeat(moveRight) { copyOfPiece.right(board); moveList.add(Move.RIGHT) }
        copyOfPiece.hardDown(); moveList.add(Move.HARD_DOWN)
        val neuralInput = srid.getBoardHeuristics()
        if (nextPiece != null) {
            val first = Pair(brain.output(neuralInput), moveList)
            val second = setBestMoveForPiece(srid, nextPiece, null)
            val score = first.first + (second?.first ?: 0.0)
            val moves = first.second
            return@withContext Pair(score, moves)
        } else {
            return@withContext Pair(brain.output(neuralInput), moveList)
        }
    }

    private fun getCopyOfColors(board: GameBoard): Array<IntArray> {
        val copy = Array(board.cols) { IntArray(board.rows) }
        for (i in 0 until board.cols) for (j in 0 until board.rows) copy[i][j] = board.colors[i][j]
        return copy
    }
}
