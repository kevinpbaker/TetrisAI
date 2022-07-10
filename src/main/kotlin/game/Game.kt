package game

import GameBoard
import GamePiece
import neural.Move
import java.util.*

interface Game {
    val shapes: MutableList<Shape>
    val shapeQueue: Stack<Shape>
    var movesInQueue: Queue<Move>
    var next: Shape?
    fun getCurrentPiece(): GamePiece?
    fun getNextPiece(): GamePiece?
    fun getBoard(): GameBoard?

    fun generateShapeQueue() {
        shapes.shuffle()
        shapeQueue.addAll(shapes)
    }

    fun loadNextShape() {
        if (shapeQueue.isEmpty()) generateShapeQueue()
        next = shapeQueue.pop()
    }
}
