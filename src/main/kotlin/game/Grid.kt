package game

import GameBoard

class Grid(
    private var p5: Tetris,
    private var x: Float,
    private var y: Float,
    private var myWidth: Float,
    private var myHeight: Float,
    override var rows: Int,
    override var cols: Int
): GameBoard {

    override var colors: Array<IntArray> = run {
        val a = Array(cols) { IntArray(rows) }
        for (i in 0 until cols) for (j in 0 until rows) a[i][j] = 0
        a
    }
    var clearedRows: ArrayList<Int> = ArrayList()
    private val animationSpeed = 64
    var animationCount = -1

    fun clear() {
        for (i in 0 until cols) for (j in 0 until rows) colors[i][j] = 0
    }

    fun draw() {
        p5.stroke(255)
        p5.strokeWeight(3f)
        p5.rect(x, y, myWidth, myHeight)
        for (i in 0 until cols) for (j in 0 until rows) drawSquare(i, j, colors[i][j])
        lineClearAnimation()
    }

    private fun lineClearAnimation() {
        if (animationCount >= 0) {
            var c = when {
                animationCount < animationSpeed -> animationCount
                else -> animationSpeed - animationCount % animationSpeed
            }
            if (clearedRows.size == 4) c = p5.color(0, c, c)
            for (row in clearedRows) for (i in 0 until cols) drawSquare(i, row, p5.color(c, 200))
            animationCount += 10
            if (animationCount > (2 * animationSpeed)) {
                animationCount = -1
                eraseCleared()
                p5.loadNext()
            }
        }
    }

    fun drawSquare(col: Int, row: Int, c: Int) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) return
        fillSquareWithTetrominoBlock(col, row, c)
        if (c == 0) addBackgroundGrid(col, row) else outlineBlockInBlack(col, row)
    }

    private fun fillSquareWithTetrominoBlock(col: Int, row: Int, c: Int) {
        p5.noStroke()
        p5.fill(c)
        drawP5Rectangle(col, row)
    }

    private fun drawP5Rectangle(col: Int, row: Int) {
        val xCoordinate = x + (col * (myWidth / cols))
        val yCoordinate = y + (row * (myHeight / rows))
        val width = myWidth / cols
        val height = myHeight / rows
        p5.rect(xCoordinate, yCoordinate, width, height)
    }

    private fun addBackgroundGrid(col: Int, row: Int) {
        p5.noFill()
        p5.stroke(255)
        p5.strokeWeight(0.1f)
        drawP5Rectangle(col, row)
    }

    private fun outlineBlockInBlack(col: Int, row: Int) {
        p5.noFill()
        p5.stroke(0)
        p5.strokeWeight(2f)
        p5.rect(x + col * (myWidth / cols), y + row * (myHeight / rows), myWidth / cols, myHeight / rows)
    }

    fun drawTetrominoLandingSpot(col: Int, row: Int, c: Int) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) return
        p5.noFill()
        p5.stroke(c)
        p5.strokeWeight(2f)
        p5.rect(x + col * (myWidth / cols), y + row * (myHeight / rows), myWidth / cols, myHeight / rows)
    }

    fun endTurn() {
        for (i in 0 until (p5.curr?.shape?.matrix?.size ?: 0))
            for (j in 0 until (p5.curr?.shape?.matrix?.size ?: 0))
                if (p5.curr?.shape?.matrix?.get(i)?.get(j) == true && ((j + (p5.curr?.y ?: 0)) >= 0)) {
                    colors[i + (p5.curr?.x ?: 0)][j + (p5.curr?.y ?: 0)] = p5.curr?.color ?: 0
                }
        if (checkLines()) {
            p5.curr = null
            animationCount = 0
        } else p5.loadNext()
    }

    private fun checkLines(): Boolean {
        clearedRows.clear()
        for (j in 0 until rows) {
            var count = 0
            for (i in 0 until cols) if (isFilled(i, j)) count++
            if (count >= cols) clearedRows.add(j)
        }
        if (clearedRows.isEmpty()) return false
        if (p5.lines / 10 < (p5.lines + clearedRows.size) / 10) {
            p5.level++
            p5.timer -= p5.speedDecrease
        }
        p5.lines += clearedRows.size
        p5.score += (1 shl clearedRows.size - 1) * 100
        return true
    }

    private fun eraseCleared() {
        for (row in clearedRows) {
            for (j in row - 1 downTo 1) {
                val rowCopy = IntArray(cols)
                for (i in 0 until cols) rowCopy[i] = colors[i][j]
                for (i in 0 until cols) colors[i][j + 1] = rowCopy[i]
            }
        }
    }
}