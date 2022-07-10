package game

import GamePiece

class Tetromino(var p5: Tetris, shape: Shape): GamePiece {

    override var shape: Shape = Shape(shape)
    override var x: Int = 3
    override var y: Int = -2
    var finalRow: Int = calculateFinalRow(p5.board)
    val color: Int
        get() = shape.colorInt

    init {
        p5.gameOver = !isValidPosition(p5.board, this.shape.matrix, 3, -1)
    }

    override fun update() {
        finalRow = calculateFinalRow(p5.board)
        // add time before block locks in
        if (y == finalRow) p5.currTime = -20
    }

    fun down() {
        when {
            y >= finalRow -> p5.board?.endTurn()
            else -> { systemStepDown(); p5.score += 1 }
        }
    }

    fun systemStepDown() {
        when {
            y >= finalRow -> p5.board?.endTurn()
            else -> { y++; p5.currTime = 0 }
        }
    }

    fun hardDown() {
        p5.score += ((p5.board?.rows ?: 0) - y)
        y = finalRow
        p5.board?.endTurn()
    }

    fun draw() {
        for (shapeMatrixX in 0 until shape.matrix.size) {
            for (shapeMatrixY in 0 until shape.matrix.size) {
                if (shape.matrix[shapeMatrixX][shapeMatrixY]) {
                    p5.board?.drawSquare(x + shapeMatrixX, y + shapeMatrixY, shape.colorInt)
                    p5.board?.drawTetrominoLandingSpot(x + shapeMatrixX, finalRow + shapeMatrixY, shape.colorInt)
                }
            }
        }
    }
}