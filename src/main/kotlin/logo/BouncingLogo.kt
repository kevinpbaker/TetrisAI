import processing.core.PApplet
import processing.core.PImage

// Bouncing DVD Logo
// Daniel Shiffman
// https://thecodingtrain.com/CodingChallenges/131-bouncing-dvd-logo.html
// https://youtu.be/0j86zuqqTlQ
// https://editor.p5js.org/codingtrain/sketches/Ya1K1ngtFk

class BouncingLogo(
    var p5: PApplet
) {
    var x = 0f; var y = 0f
    var xspeed = 0; var yspeed = 0
    var r = 0f; var g = 0f; var b = 0f
    var logo: PImage? = null

    fun setup() {
        logo = p5.loadImage("src/main/kotlin/logo/tetris.png")
        val p5Width = p5.width.toFloat()
        val multiplier = when {
            p5Width <= 100 -> 0.05
            p5Width <= 200 -> 0.10
            p5Width <= 300 -> 0.15
            p5Width <= 400 -> 0.20
            p5Width <= 500 -> 0.225
            p5Width < 600 -> 0.25
            else -> 0.40
        }
        logo?.resize((439 * multiplier).toInt(), (146f * multiplier).toInt())
        x = p5.random(p5.width.toFloat()); y = p5.random(p5.height.toFloat())
        xspeed = 5; yspeed = 5
        pickColor()
    }

    fun pickColor() {
        r = p5.random(100f, 256f); g = p5.random(100f, 256f); b = p5.random(100f, 256f)
    }

    fun draw() {
        p5.tint(r, g, b)
        p5.image(logo, x, y)
        x += xspeed; y += yspeed
        val logoWidth = (logo?.width ?: 0).toFloat()
        val logoHeight = (logo?.height ?: 0).toFloat()
        val logoXPosition = x + logoWidth
        val logoYPosition = y + logoHeight
        val p5Width = p5.width.toFloat()
        val p5Height = p5.height.toFloat()
        checkXPosition(logoXPosition, p5Width, logoWidth)
        checkYPosition(logoYPosition, p5Height, logoHeight)
    }

    private fun checkXPosition(logoXPosition: Float, p5Width: Float, logoWidth: Float) {
        if (logoXPosition >= p5.width) {
            xspeed = -xspeed
            x = p5Width - logoWidth
            pickColor()
        } else if (x <= 0) {
            xspeed = -xspeed
            x = 0f
            pickColor()
        }
    }

    private fun checkYPosition(logoYPosition: Float, p5Height: Float, logoHeight: Float) {
        if (logoYPosition >= p5Height) {
            yspeed = -yspeed
            y = p5Height - logoHeight
            pickColor()
        } else if (y <= 0) {
            yspeed = -yspeed
            y = 0f
            pickColor()
        }
    }


}