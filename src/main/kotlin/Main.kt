import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener
import com.jogamp.newt.event.WindowAdapter
import com.jogamp.newt.event.WindowEvent
import com.jogamp.newt.opengl.GLWindow
import com.jogamp.opengl.*
import com.jogamp.opengl.util.Animator
import com.jogamp.opengl.util.texture.Texture
import com.jogamp.opengl.util.texture.TextureIO
import kotlin.math.max
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val window = MainWindow()
    val tetris = Tetris(object: Tetris.Callbacks {
        override fun onViewUpdated(state: TetrisViewState) {
            window.draw(state)
        }

        override fun onGameOver() {
        }
    })
    window.onOperation = { tetris.operation(it) }
    window.create()
    tetris.start()
}


class MainWindow {

    var onOperation: (Operation) -> Unit = {}
    private lateinit var animator: Animator

    fun create() {
        val caps = GLCapabilities(GLProfile.get(GLProfile.GL2))

        val window = GLWindow.create(caps).apply {
            title = "test window"
            setSize(800, 800)
            addWindowListener(object: WindowAdapter() {
                override fun windowDestroyed(e: WindowEvent?) {
                    exitProcess(0)
                }
            })
            addKeyListener(object: KeyListener {
                override fun keyPressed(e: KeyEvent?) {
                    val operation = when(e?.keyCode) {
                        KeyEvent.VK_LEFT -> Operation.LEFT
                        KeyEvent.VK_RIGHT -> Operation.RIGHT
                        KeyEvent.VK_DOWN -> Operation.DOWN
                        KeyEvent.VK_UP -> Operation.HARD_DROP
                        KeyEvent.VK_Z -> Operation.CLOCKWISE
                        KeyEvent.VK_X -> Operation.COUNTERCLOCKWISE
                        KeyEvent.VK_SPACE -> Operation.HOLD
                        else -> null
                    }
                    operation?.let { onOperation(it) }
                }

                override fun keyReleased(e: KeyEvent?) {
                }

            })
            addGLEventListener(windowListener)
            animator = Animator()
            animator.add(this)
            animator.start()
            isVisible = true
        }
    }

    private var state: TetrisViewState? = null
    fun draw(state: TetrisViewState) {
        this.state = state
    }


    private lateinit var tex: Texture
    private lateinit var fieldTex: Texture

    private fun initTexture() {
        tex = TextureIO.newTexture(javaClass.getResourceAsStream("tex.png"), false, TextureIO.PNG)
        fieldTex = TextureIO.newTexture(javaClass.getResourceAsStream("f.png"), false, TextureIO.PNG)
    }


    private fun getTextureCord(color: Color): List<Vector2Df> {
        return when(color) {
            Color.Cyan -> listOf(Vector2Df(.25f, .5f), Vector2Df(.5f, .5f), Vector2Df(.5f, .75f), Vector2Df(.25f, .75f))
            Color.Yellow -> listOf(Vector2Df(.75f, .75f), Vector2Df(1f, .75f), Vector2Df(1f, 1f), Vector2Df(.75f, .75f))
            Color.Purple -> listOf(Vector2Df(0f, .5f), Vector2Df(.25f, .5f), Vector2Df(.25f, .75f), Vector2Df(0f, .75f))
            Color.Blue -> listOf(Vector2Df(.5f, .75f), Vector2Df(.75f, .75f), Vector2Df(.75f, 1f), Vector2Df(.5f, 1f))
            Color.Orange -> listOf(Vector2Df(.75f, .5f), Vector2Df(1f, .5f), Vector2Df(1f, .75f), Vector2Df(.75f, .75f))
            Color.Green -> listOf(Vector2Df(.25f, .75f), Vector2Df(.5f, .75f), Vector2Df(.5f, 1f), Vector2Df(.25f, 1f))
            Color.Red -> listOf(Vector2Df(.5f, .5f), Vector2Df(.75f, .5f), Vector2Df(.75f, .75f), Vector2Df(.5f, .75f))
        }
    }

    private fun getGhostCord(color: Color): List<Vector2Df> {
        return when(color) {
            Color.Cyan -> listOf(Vector2Df(.25f, 0f), Vector2Df(.5f, 0f), Vector2Df(.5f, .25f), Vector2Df(.25f, .25f))
            Color.Yellow -> listOf(Vector2Df(.75f, .25f), Vector2Df(1f, .25f), Vector2Df(1f, .5f), Vector2Df(.75f, .5f))
            Color.Purple -> listOf(Vector2Df(0f, 0f), Vector2Df(.25f, 0f), Vector2Df(.25f, .25f), Vector2Df(0f, .25f))
            Color.Blue -> listOf(Vector2Df(.5f, .25f), Vector2Df(.75f, .25f), Vector2Df(.75f, .5f), Vector2Df(.5f, .5f))
            Color.Orange -> listOf(Vector2Df(.75f, 0f), Vector2Df(1f, 0f), Vector2Df(1f, .25f), Vector2Df(.75f, .25f))
            Color.Green -> listOf(
                Vector2Df(.25f, .25f),
                Vector2Df(.5f, .25f),
                Vector2Df(.5f, .5f),
                Vector2Df(.25f, .5f)
            )
            Color.Red -> listOf(Vector2Df(.5f, 0f), Vector2Df(.75f, 0f), Vector2Df(.75f, .25f), Vector2Df(.5f, .25f))
        }
    }

    private val windowListener = object: GLEventListener {
        override fun init(drawable: GLAutoDrawable?) {
            initTexture()
            drawable?.gl?.gL2?.apply {
                glClearColor(0f, 0f, 0.0f, 1f)
                glLoadIdentity()
                glEnable(GL2.GL_TEXTURE_2D)
                glEnable(GL2.GL_BLEND)
                glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)
                fieldTex.enable(this)
                tex.enable(this)
            }

        }

        override fun dispose(drawable: GLAutoDrawable?) {
            drawable?.gl?.gL2?.let { gl ->
                tex.destroy(gl)
            }
            if(::animator.isInitialized)
                animator.stop()
        }

        override fun display(drawable: GLAutoDrawable?) {
            val gl = drawable?.gl?.gL2 ?: return
            val state = state ?: return
            gl.glClear(GL.GL_COLOR_BUFFER_BIT)
            val xSize = state.fieldView.field.first().size
            val ySize = state.fieldView.field.size
            val cellSize = 1.6f/max(xSize, ySize).toFloat()
            //val offset = -.8f
            val offset = Vector2Df(-.7f, -.8f)


            drawFieldBackground(gl, offset, cellSize)
            gl.glBindTexture(GL.GL_TEXTURE_2D, tex.textureObject)
            drawNext(state, gl, offset, cellSize)
            drawHold(state, gl, offset, cellSize)
            state.fieldView.field.forEachIndexed { y, row ->
                row.forEachIndexed { x, color ->
                    if(color != null) {
                        drawBlockTexture(gl, offset, cellSize, x, y, getTextureCord(color))
                    }
                }
            }
            state.fieldView.ghosts.forEach {
                val (x, y) = it.first
                val color = it.second
                drawBlockTexture(gl, offset, cellSize, x, y, getGhostCord(color))
            }
            state.fieldView.current.forEach {
                val (x, y) = it.first
                val color = it.second
                drawBlockTexture(gl, offset, cellSize, x, y, getTextureCord(color))
            }
            gl.glFlush()
        }

        override fun reshape(drawable: GLAutoDrawable?, x: Int, y: Int, width: Int, height: Int) {
        }

        private fun drawFieldBackground(gl: GL2, offset: Vector2Df, cellSize: Float) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, fieldTex.textureObject)
            gl.glBegin(GL2.GL_QUADS)
            gl.glTexCoord2f(0f, 0f)
            gl.glVertex2f(offset.x - cellSize*3f, offset.y - cellSize)
            gl.glTexCoord2f(1f, 0f)
            gl.glVertex2f(offset.x + cellSize*13f, offset.y - cellSize)
            gl.glTexCoord2f(1f, .6875f)
            gl.glVertex2f(offset.x + cellSize*13f, offset.y + cellSize*21)
            gl.glTexCoord2f(0f, .6875f)
            gl.glVertex2f(offset.x - cellSize*3f, offset.y + cellSize*21)
            gl.glEnd()
        }

        private fun drawNext(state: TetrisViewState, gl: GL2, offset: Vector2Df, cellSize: Float) {
            val ratio = .5f
            state.next.forEachIndexed { index, next ->
                val coords = getTextureCord(next.color)
                next.getRotate(RotateOrientation.R0).forEach { pos ->
                    drawBlockTexture(gl, offset.x+cellSize*11f+cellSize*ratio*pos.x, offset.y+cellSize*18f+cellSize*ratio*pos.y-index*4f*cellSize*ratio, coords, cellSize*ratio)
                }
            }
        }

        private fun drawHold(state: TetrisViewState, gl: GL2, offset: Vector2Df, cellSize: Float) {
            val ratio = .5f
            val coords = state.hold?.color?.let { getTextureCord(it) } ?: return
            state.hold.getRotate(RotateOrientation.R0).forEach { pos ->
                drawBlockTexture(gl, offset.x+cellSize*-2.5f+cellSize*ratio*pos.x, offset.y+cellSize*18f+cellSize*ratio*pos.y, coords, cellSize*ratio)
            }
        }
    }
}

private fun drawBlockTexture(
    gl: GL2,
    offset: Vector2Df,
    cellSize: Float,
    x: Int,
    y: Int,
    coords: List<Vector2Df>
) {
    drawBlockTexture(gl, offset.x+x*cellSize, offset.y+y*cellSize, coords, cellSize)
}

private fun drawBlockTexture(gl: GL2, x:Float, y:Float, coords: List<Vector2Df>, displaySize: Float) {
    gl.glBegin(GL2.GL_QUADS)
    gl.glTexCoord2f(coords[0].x, coords[0].y)
    gl.glVertex2f(x,y)
    gl.glTexCoord2f(coords[1].x, coords[1].y)
    gl.glVertex2f(x + displaySize, y)
    gl.glTexCoord2f(coords[2].x, coords[2].y)
    gl.glVertex2f(x + displaySize, y + displaySize)
    gl.glTexCoord2f(coords[3].x, coords[3].y)
    gl.glVertex2f(x, y + displaySize)
    gl.glEnd()
}

private data class Vector2Df(
    val x: Float,
    val y: Float
)