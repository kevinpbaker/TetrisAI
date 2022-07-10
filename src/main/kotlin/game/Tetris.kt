package game
import BouncingLogo
import GameBoard
import GamePiece
import default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import neural.Individual
import neural.Move
import processing.core.PApplet
import processing.core.PFont
import tetrisGameWidth
import java.util.*


class Tetris : Game, PApplet() {

    companion object Factory {
        suspend fun run(i: Individual): Tetris {
            val t = Tetris()
            t.player = i; t.play(); t.runSketch()
            while(t.playing) delay(100)
            return t
        }
    }

    var playing: Boolean = true

    /* Game Control ------------------------------------------ */
    private val cyan:   Int = color(0, 255, 255)
    private val orange: Int = color(255, 165, 0)
    private val yellow: Int = color(255, 255, 0)
    private val purple: Int = color(160, 32, 240)
    private val blue:   Int = color(0, 0, 255)
    private val red:    Int = color(255, 0, 0)
    private val green:  Int = color(0, 255, 0)

    lateinit var player: Individual
    var board: Grid? = null
    var preview: Grid? = null
    var curr: Tetromino? = null
    var tetrisFont: PFont? = null
    override var next: Shape? = null
    override var shapes = mutableListOf(
        Shape(4, intArrayOf(8, 9, 10, 11), cyan,   ShapeType.I),          // I
        Shape(3, intArrayOf(0, 3, 4, 5),   blue,   ShapeType.RIGHT_GUN),  // J
        Shape(3, intArrayOf(2, 3, 4, 5),   orange, ShapeType.LEFT_GUN),   // L
        Shape(2, intArrayOf(0, 1, 2, 3),   yellow, ShapeType.SQUARE),     // O
        Shape(4, intArrayOf(5, 6, 8, 9),   green,  ShapeType.LEFT_SNAKE), // S
        Shape(3, intArrayOf(1, 3, 4, 5),   purple, ShapeType.T),          // T
        Shape(4, intArrayOf(4, 5, 9, 10),  red,    ShapeType.RIGHT_SNAKE) // Z
    )
    override var shapeQueue: Stack<Shape> = Stack()
    var timer = 1
    var currTime = 0
    var score = 0
    var bestScore = 0
    var lines = 0
    var level = 1
    var timeAlive = 0
    val speedDecrease = 2
    var gameOver = false
    /* ------------------------------------------ Game Control */

    /* AI ------------------------------------------ */
    override var movesInQueue: Queue<Move> = LinkedList()
    override fun getCurrentPiece(): GamePiece? = curr
    override fun getBoard(): GameBoard? = board
    override fun getNextPiece(): GamePiece? {
        next?.let { return Tetromino(this, it) }
        return null
    }
    /* ------------------------------------------ AI */

    private val bouncingLogo: BouncingLogo = BouncingLogo(this)

    override fun settings() {
        super.settings()
        val width = tetrisGameWidth
        size(width, (width*1.38f).toInt(), JAVA2D)
    }

    override fun setup() {
        bouncingLogo.setup()
        tetrisFont = createFont("src/main/kotlin/game/fonts/Futura_Std_Bold_Condensed.otf", 64f)
        textFont(tetrisFont)
        textSize(width*0.05f)
        board = Grid(this, width*0.04f, width*0.04f, width*0.642f, height*0.92f, 20, 10)
        preview = Grid(this, width*0.71f, width*0.04f, width*0.232f, height*0.084f, 2, 4)
        loadNextShape()
        loadNext()
    }

    override fun draw() {
        when {
            gameOver -> gameOverDrawing()
            else -> drawGameBoard()
        }
    }

    private fun gameOverDrawing() {
        frameRate(30f)
        background(0)
        bouncingLogo.draw()
        textAlign(CENTER)
        val gameOverText = "GAME OVER\nSCORE: $score"
        text(gameOverText, (width/2).toFloat(), (height*0.15).toFloat() - (height*0.071f))
        val simulatingGamesText = "SIMULATING TETRIS GAMES\nBREEDING BEST PLAYERS"
        text(simulatingGamesText, (width/2).toFloat(), (height*0.85).toFloat() - (height*0.071f))
        playing = false
    }

    private fun drawGameBoard() {
        background(0)
        frameRate(60f)
        currTime++
        if (currTime >= timer && board?.animationCount == -1) {
            default.launch(Dispatchers.Unconfined) { neuralInput(player) }
            timeAlive++
            player.timeAlive = timeAlive
            player.score = score.toDouble()
            player.lines = lines
            player.level = level
            curr?.systemStepDown()
        }
        preview?.draw()
        board?.draw()
        if (curr != null) curr?.draw()
        next?.preview(this@Tetris)
        fill(255)
        textAlign(LEFT)
        text("LEVEL\n$level", width*0.7f, height*0.174f)
        text("LINES\n$lines", width*0.7f, height*0.29f)
        text("SCORE\n$score", width*0.7f, height*0.405f)
        text("HIGH SCORE\n$bestScore", width*0.7f, height*0.53f)
    }

    fun loadNext() {
        next?.let { curr = Tetromino(this, it) }
        loadNextShape()
        currTime = 0
    }

    private suspend fun neuralInput(i: Individual?) {
        i ?: return
        if (movesInQueue.isNotEmpty()) {
            when (movesInQueue.poll()) {
                Move.LEFT -> curr?.left(board)
                Move.RIGHT -> curr?.right(board)
                Move.ROTATE -> curr?.rotate(board)
                Move.HARD_DOWN -> curr?.hardDown()
                else -> { }
            }
        } else {
            calculateMovement(i)
        }
    }

    private suspend fun calculateMovement(i: Individual) {
        i.calculateBestMove(this)?.second?.let { this.movesInQueue = it }
    }

    override fun keyPressed() {
        if (curr == null || gameOver) return
        when (keyCode) {
            LEFT -> curr?.left(board)
            RIGHT -> curr?.right(board)
            UP -> curr?.rotate(board)
            DOWN -> curr?.down()
            ' '.code -> curr?.hardDown()
        }
    }

    fun play() { reset() }

    private fun reset() {
        board?.clear(); gameOver = false; playing = true
        timer = 1 // was 20
        if (score > bestScore) bestScore = score
        currTime = 0; score = 0; lines = 0
        level = 1; timeAlive = 0
        loop(); loadNext()
    }

    suspend fun runAgain(i: Individual): Boolean {
        this.player = i
        play()
        while(playing) kotlinx.coroutines.delay(100)
        return true
    }

}