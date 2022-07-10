package simulation

import GameBoard
import koma.fill
import koma.matrix.Matrix

class Srid(
    override val rows: Int,
    override val cols: Int,
    override var colors: Array<IntArray>
): GameBoard {
    var clearedRows: ArrayList<Int> = ArrayList()

    fun fillBoard(setromino: Setromino) {
        for (col in 0 until (setromino.shape.matrix.size))
            for (row in 0 until (setromino.shape.matrix.size))
                if (setromino.shape.matrix[col][row] && ((row + (setromino.y)) >= 0)) {
                    colors[col + (setromino.x)][row + (setromino.y)] = 1
                }
    }

    fun howManyLinesCleared(): Int {
        clearedRows.clear()
        for (j in 0 until rows) {
            var count = 0
            for (i in 0 until cols) if (isFilled(i, j)) count++
            if (count >= cols) clearedRows.add(j)
        }
        if (clearedRows.isEmpty()) return 0
        return clearedRows.size
    }

    fun getBoardHeuristics(): Matrix<Double> {
        return fill(4, 1) { rowIndex, _ ->
            when (rowIndex) {
                0 -> aggregatedHeight().toDouble()
                1 -> howManyLinesCleared().toDouble()
                2 -> nHoles().toDouble()
                3 -> getBumpiness().toDouble()
                else -> 0.0
            }
        }
    }

    fun clearLinesAndReturnScoreToAdd(): Double {
        val scored = nextScore()
        clearLinesFromGame()
        return scored.toDouble()
    }

    private fun nextScore(): Int {
        return when(howManyLinesCleared()) {
            1 -> 100
            2 -> 200
            3 -> 400
            4 -> 800
            5 -> 1600
            6 -> 3200
            7 -> 6400
            8 -> 12800
            else -> 0
        }
    }

    private fun clearLinesFromGame() {
        for (row in clearedRows) {
            for (j in row - 1 downTo 1) {
                val rowCopy = IntArray(cols)
                for (i in 0 until cols) rowCopy[i] = colors[i][j]
                for (i in 0 until cols) colors[i][j + 1] = rowCopy[i]
            }
        }
    }

    override fun toString(): String {
        var string = "CURRENT BOARD\n"
        this.colors.forEach {
            it.forEach { string += "${if (it != 0) 1 else 0} " }
            string += "\n"
        }
        string += "\n"
        return string
    }
}