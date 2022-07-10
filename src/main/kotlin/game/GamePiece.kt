import game.Shape
import kotlin.math.max

interface GamePiece {
    var shape: Shape
    var x: Int
    var y: Int

    fun update()

    fun left(board: GameBoard?) {
        when {
            isValidPosition(board, shape.matrix, x - 1, y) -> x--
            isValidPosition(board, shape.matrix, x - 2, y) -> x -= 2
        }
        update()
    }

    fun right(board: GameBoard?) {
        when {
            isValidPosition(board, shape.matrix, x + 1, y) -> x++
            isValidPosition(board, shape.matrix, x + 2, y) -> x += 2
        }
        update()
    }

    fun isValidPosition(board: GameBoard?, matrix: Array<BooleanArray>, col: Int, row: Int): Boolean {
        for (i in matrix.indices) {
            for (j in matrix.indices) {
                val tetrominoNewPosition = matrix[i][j]
                val spotAlreadyTaken = board?.isFilled(col + i, row + j) ?: false
                if (tetrominoNewPosition && spotAlreadyTaken) return false
            }
        }
        return true
    }

    fun rotate(board: GameBoard?) {
        // rotate the 2D matrix clockwise
        val rotatedShape = Array(shape.matrix.size) { BooleanArray(shape.matrix.size) }
        for (x in rotatedShape.indices) {
            for (y in rotatedShape.indices) {
                val xRotation = y
                val endIndex = rotatedShape.size - 1
                val yRotation = endIndex - x
                rotatedShape[x][y] = shape.matrix[xRotation][yRotation]
            }
        }
        fun valid(i: Int, shape: Array<BooleanArray> = rotatedShape, j: Int = y): Boolean {
            return isValidPosition(board, shape, i, j)
        }
        // check for legal rotation or adjust piece back onto board
        when {
            valid(x) -> { shape.matrix = rotatedShape; update() }
            valid(x + 1) || valid(x + 2) -> { shape.matrix = rotatedShape; right(board) }
            valid(x - 1) || valid(x - 2) -> { shape.matrix = rotatedShape; left(board) }
        }
    }

    fun calculateFinalRow(board: GameBoard?): Int {
        val start: Int = max(0, y)
        for (row in start..(board?.rows ?: 0)) if (!isValidPosition(board, shape.matrix, x, row)) return row - 1
        return -1
    }
}