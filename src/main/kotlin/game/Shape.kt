package game

class Shape {
    var matrix: Array<BooleanArray>
    var colorInt: Int
    var type: ShapeType

    constructor(n: Int, blockPosition: IntArray, colorInt: Int, type: ShapeType) {
        matrix = Array(n) { BooleanArray(n) }
        for (col in 0 until n) for (row in 0 until n) matrix[col][row] = false
        for (i in blockPosition.indices) {
            val colTranslation = blockPosition[i] % n
            val rowTranslation = blockPosition[i] / n
            matrix[colTranslation][rowTranslation] = true
        }
        this.colorInt = colorInt
        this.type = type
    }

    constructor(other: Shape) {
        matrix = Array(other.matrix.size) { BooleanArray(other.matrix.size) }
        for (col in matrix.indices) for (row in matrix.indices) matrix[col][row] = other.matrix[col][row]
        colorInt = other.colorInt
        this.type = other.type
    }

    fun preview(p5: Tetris) {
        // adjust for 4X2 preview
        var startingRow = 1
        for (col in matrix.indices) if (matrix[col][0]) startingRow = 0
        for (col in matrix.indices)
            for (row in startingRow until matrix.size)
                if (matrix[col][row]) p5.preview?.drawSquare(col, row - startingRow, colorInt)
    }
}
