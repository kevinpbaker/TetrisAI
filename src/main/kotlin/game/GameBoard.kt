import koma.abs
import koma.ceil
import koma.extensions.forEachIndexed
import koma.extensions.get
import koma.fill
import koma.floor
import koma.matrix.Matrix

interface GameBoard {
    val rows: Int
    val cols: Int
    var colors: Array<IntArray>

    fun isFilled(x: Int, y: Int): Boolean {
        return when {
            (y < 0) && (x < cols) && (x >= 0) -> false
            else -> (x >= cols) || (x < 0) || (y >= rows) || (colors[x][y] != 0)
        }
    }

    fun getBoardAsMatrix(): Matrix<Double> {
        return fill(rows, cols) { row, col ->
            if (isFilled(col, row)) 1.0 else 0.0
        }
    }

    fun getPeaks(): List<Int> {
        return getBoardAsMatrix().mapColsToList {
            val indexes = mutableListOf<Int>()
            it.forEachIndexed { rowIndex, _, ele ->
                when {
                    ele > 0 -> {
                        val endRowIndex = rows - 1
                        indexes.add(endRowIndex - rowIndex)
                    }
                    else -> indexes.add(0)
                }
            }
            return@mapColsToList indexes.maxOrNull() ?: 0
        }
    }

    fun nPits(): Int {
        return getPeaks().sumOf { (if (it == 0) return 1 else 0) as Int }
    }

    fun highestPeak(): Int {
        return getPeaks().maxOrNull() ?: 0
    }

    fun lowestPeak(): Int {
        return getPeaks().minOrNull() ?: 0
    }

    fun aggregatedHeight(): Int {
        return getPeaks().sum()
    }

    fun getHolesPerColumn(): List<Int> {
        val peaks = getPeaks()
        val board = getBoardAsMatrix()
        val holes = mutableListOf<Int>()
        repeat(board.numCols()) { holes.add(0) }
        board.forEachIndexed { rowIndex, colIndex, _ ->
            val endRowIndex = rows - 1
            val isBelowPeak = rowIndex > (endRowIndex - peaks[colIndex])
            if (isBelowPeak && !isFilled(colIndex, rowIndex)) holes[colIndex] += 1
        }
        return holes
    }

    fun nHoles(): Int {
        return getHolesPerColumn().sum()
    }

    fun getBumpiness(): Int {
        val p = getPeaks()
        var bumpiness = 0
        for (i in 0 until cols-2) {
            bumpiness += abs(p[i] - p[i+1]).toInt()
        }
        return bumpiness
    }

    fun nColsWithHoles(): Int {
        return getHolesPerColumn().sumOf { (if (it > 0) return 1 else 0) as Int }
    }

    fun getBoardRowBalance(): Double {
        // higher row is lower on board
        return fill(rows, cols) { row, col ->
            if (isFilled(col, row)) {
                when (row) {
                    in 0..floor(row/2.0) -> { -25.0 }
                    in ceil(row*(3/4.0))..(rows) -> { (row*2).toDouble() }
                    else -> 0.0
                }

            } else 0.0
        }.elementSum()
    }

    fun getRowTransitions(): Int {
        val highest = (rows-1) - highestPeak()
        val board = getBoardAsMatrix()
        var rowTransitions = 0
        for (r in 0 until highest) {
            val row = board.getRow((rows-1)-r)
            var current = row[0]
            for (x in 0 until (cols-1)) {
                if (row[x+1] != current) rowTransitions++
                current = row[x+1]
            }
        }
        return rowTransitions
    }

    fun getColumnTransitions(): Int {
        val highest = (rows-1) - highestPeak()
        val board = getBoardAsMatrix()
        var colTransitions = 0
        for (c in 0 until cols) {
            val col = board.getCol(c)
            for (h in (rows-1)-highest until rows-1) {
                var current = col[h]
                if (col[h+1] != current) colTransitions++
                current = col[h+1]
            }
        }
        return colTransitions
    }
}