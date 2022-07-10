package simulation

import GamePiece
import game.Shape

class Setromino(val srid: Srid, shape: Shape): GamePiece {

    override var shape: Shape = Shape(shape)
    override var x: Int = 3
    override var y: Int = -2
    private var finalRow: Int = calculateFinalRow(srid)

    override fun update() {
        finalRow = calculateFinalRow(srid)
    }

    // move block all the way to the bottom
    fun hardDown(): Double {
        val s = ((srid.rows) - y)
        y = finalRow
        srid.fillBoard(this)
        return s.toDouble()
    }
}