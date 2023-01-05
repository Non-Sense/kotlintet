import kotlin.math.min

enum class ScoreFlag {
    Single,
    Double,
    Triple,
    Tetris,

}

data class TetrisViewState(
    val fieldView: FieldView,
    val hold: Mino?,
    val level: Int,
    val next: List<Mino>
)

private class RotateTest {
    companion object {
        private val minoRotateTest: List<List<Vector2D>> = listOf(
            listOf(Vector2D(0, 0), Vector2D(-1, 0), Vector2D(-1, 1), Vector2D(0, -2), Vector2D(-1, -2)),
            listOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(1, -1), Vector2D(0, 2), Vector2D(1, 2)),
            listOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(1, -1), Vector2D(0, 2), Vector2D(1, 2)),
            listOf(Vector2D(0, 0), Vector2D(-1, 0), Vector2D(-1, 1), Vector2D(0, -2), Vector2D(-1, -2)),
            listOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(1, 1), Vector2D(0, -2), Vector2D(1, -2)),
            listOf(Vector2D(0, 0), Vector2D(-1, 0), Vector2D(-1, -1), Vector2D(0, 2), Vector2D(-1, 2)),
            listOf(Vector2D(0, 0), Vector2D(-1, 0), Vector2D(-1, -1), Vector2D(0, 2), Vector2D(-1, 2)),
            listOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(1, 1), Vector2D(0, -2), Vector2D(1, -2))
        )

        private val iMinoRotateTest = listOf(
            listOf(Vector2D(0, 0), Vector2D(-2, 0), Vector2D(1, 0), Vector2D(-2, -1), Vector2D(1, 2)),
            listOf(Vector2D(0, 0), Vector2D(2, 0), Vector2D(-1, 0), Vector2D(2, 1), Vector2D(-1, -2)),
            listOf(Vector2D(0, 0), Vector2D(-1, 0), Vector2D(2, 0), Vector2D(-1, 2), Vector2D(2, -1)),
            listOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(-2, 0), Vector2D(1, -2), Vector2D(-2, 1)),
            listOf(Vector2D(0, 0), Vector2D(2, 0), Vector2D(-1, 0), Vector2D(2, 1), Vector2D(-1, -2)),
            listOf(Vector2D(0, 0), Vector2D(-2, 0), Vector2D(1, 0), Vector2D(-2, -1), Vector2D(1, 2)),
            listOf(Vector2D(0, 0), Vector2D(1, 0), Vector2D(-2, 0), Vector2D(1, -2), Vector2D(-2, 1)),
            listOf(Vector2D(0, 0), Vector2D(-1, 0), Vector2D(2, 0), Vector2D(-1, 2), Vector2D(2, -1))
        )

        private fun getRotateTestIndex(current: RotateOrientation, next: RotateOrientation): Int {
            return when(current) {
                RotateOrientation.R0 -> if(next == RotateOrientation.R90) 0 else 7
                RotateOrientation.R90 -> if(next == RotateOrientation.R0) 1 else 2
                RotateOrientation.R180 -> if(next == RotateOrientation.R90) 3 else 4
                RotateOrientation.R270 -> if(next == RotateOrientation.R180) 5 else 6
            }
        }

        fun getTest(mino: Mino, current: RotateOrientation, next: RotateOrientation):List<Vector2D> {
            val index = getRotateTestIndex(current, next)
            if(mino == Mino.I)
                return iMinoRotateTest[index]
            return minoRotateTest[index]
        }
    }
}

class Tetris(
    private val callbacks: Callbacks
) {

    companion object {
        private val fallSpeed = listOf(1000, 793, 618, 473, 355, 262, 190, 135, 94, 64, 43, 28, 18, 11, 7)
        private const val nextVisibleNum = 6
    }

    private val random = java.util.Random()
    private val field = Field()
    private var startTime: Long = 0L
    private var level = 0
    private var hold: Mino? = null
    private val next: ArrayDeque<Mino> = ArrayDeque()
    private var minoState = MinoState(Mino.I, Field.spawnPosition, RotateOrientation.R0)

    interface Callbacks {
        fun onViewUpdated(state: TetrisViewState)
        fun onGameOver()
    }

    private fun addNext() {
        next.addAll(Mino.values.shuffled(random))
    }

    private fun getNextView(): List<Mino> {
        return next.subList(0, min(nextVisibleNum, next.size))
    }

    fun start() {
        field.clear()
        hold = null
        next.clear()
        addNext()
        addNext()
        startTime = System.currentTimeMillis()
        spawn(null)
    }

    private fun spawn(nextMino: Mino?) {
        minoState = MinoState(nextMino ?: next.removeFirst(), Field.spawnPosition, RotateOrientation.R0)
        if(next.size <= nextVisibleNum)
            addNext()
        if(field.isCollision(minoState)) {
            gameOver()
            return
        }
        viewUpdate()
    }

    fun operation(operation: Operation) {
        when(operation) {
            Operation.DOWN -> move(Vector2D(0, -1))
            Operation.LEFT -> move(Vector2D(-1, 0))
            Operation.RIGHT -> move(Vector2D(1, 0))
            Operation.CLOCKWISE -> rotate(true)
            Operation.COUNTERCLOCKWISE -> rotate(false)
            Operation.HOLD -> hold()
            Operation.HARD_DROP -> hardDrop()
        }
        viewUpdate()
    }

    private fun hold() {
        val next = hold
        hold = minoState.mino
        spawn(next)
    }

    private fun hardDrop() {
        while(true) {
            if(!move(Vector2D(0, -1)))
                break
        }
        setMino()
    }

    private fun setMino() {
        if(!field.set(minoState))
            callbacks.onGameOver()
        lineDeleted(field.deleteLines())
        spawn(null)
    }

    private fun lineDeleted(lineNum: Int) {

    }

    private fun move(direction: Vector2D): Boolean {
        val newState = minoState.copy(position = minoState.position + direction)
        if(!field.isCollision(newState)) {
            minoState = newState
            return true
        }
        return false
    }

    private fun rotate(isClockwise: Boolean) {
        val nextOrientation = minoState.orientation.getNext(isClockwise)
        val rawRotate = minoState.copy(orientation = nextOrientation)
        if(!field.isCollision(rawRotate)){
            minoState = rawRotate
            return
        }
        val tests = RotateTest.getTest(minoState.mino, minoState.orientation, nextOrientation)
        tests.forEach {
            val newState = rawRotate.copy(position = rawRotate.position + it)
            if(!field.isCollision(newState)){
                minoState = newState
                return
            }
        }
    }

    private fun viewUpdate() {
        callbacks.onViewUpdated(TetrisViewState(field.getView(minoState), hold, level, getNextView()))
    }

    private fun gameOver() {
        callbacks.onGameOver()
    }

}

data class FieldView(
    val field: List<List<Color?>>,
    val current: List<Pair<Vector2D, Color>>,
    val ghosts: List<Pair<Vector2D, Color>>
)

private data class MinoState(
    val mino: Mino,
    val position: Vector2D,
    val orientation: RotateOrientation
) {
    val rotatedMino = mino.getRotate(orientation)
}

private class Field {
    companion object {
        private const val VISIBLE_HEIGHT = 20
        private const val HEIGHT = 40
        private const val WIDTH = 10
        val spawnPosition = Vector2D(WIDTH/2 - 2, VISIBLE_HEIGHT - 1)
    }

    private val field: Array<Array<Color?>> = Array(HEIGHT) { Array(WIDTH) { null } }

    fun isCollision(state: MinoState): Boolean {
        state.rotatedMino.forEach {
            val p = state.position + it
            if(p.x < 0 || p.x >= WIDTH)
                return true
            if(p.y < 0 || p.y >= HEIGHT)
                return true
            if(field[p.y][p.x] != null)
                return true
        }
        return false
    }

    fun set(state: MinoState): Boolean {
        if(isCollision(state))
            return false
        var isValid = true
        for(it in state.rotatedMino) {
            val p = state.position + it
            if(p.x < 0 || p.x >= WIDTH)
                continue
            if(p.y < 0 || p.y >= HEIGHT)
                continue
            field[p.y][p.x] = state.mino.color
            isValid = isValid.and(p.y < VISIBLE_HEIGHT)
        }
        return isValid
    }

    fun deleteLines(): Int {
        val deletedLines = mutableListOf<Int>()
        field.forEachIndexed { index, row ->
            if(row.all { it != null })
                deletedLines += index
        }
        if(deletedLines.isEmpty())
            return 0
        var currentY = 0
        for(y in field.indices) {
            if(deletedLines.contains(y)) {
                currentY++
                continue
            }
            field[y - currentY] = field[y].copyOf()
        }
        return deletedLines.size
    }

    fun clear() {
        field.forEach {
            it.fill(null)
        }
    }

    fun getView(state: MinoState): FieldView {
        val bottom = getBottom(state)
        val ghosts = state.rotatedMino.map {
            (bottom + it) to state.mino.color
        }
        val f = field.sliceArray(0 until VISIBLE_HEIGHT).toList().map { it.toList() }
        val current = state.rotatedMino.map {
            (state.position + it) to state.mino.color
        }
        return FieldView(f, current, ghosts)
    }

    private fun getBottom(state: MinoState): Vector2D {
        var pos = state.position
        while(true) {
            val next = pos + Vector2D(0, -1)
            if(isCollision(state.copy(position = next)))
                return pos
            pos = next
        }
    }
}

