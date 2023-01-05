import kotlin.reflect.full.isSubclassOf

sealed class Mino {
    companion object {
        val values by lazy {
            Mino::class.nestedClasses.filter {
                it.isFinal && it.isSubclassOf(Mino::class)
            }.map { it.objectInstance as Mino }
        }
    }

    abstract val color: Color
    abstract fun getRotate(orientation: RotateOrientation): Set<Vector2D>

    object I: Mino() {
        override val color = Color.Cyan
        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
            RotateOrientation.R0 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(3, 1)
            )
            RotateOrientation.R90 -> setOf(
                Vector2D(2, -1),
                Vector2D(2, 0),
                Vector2D(2, 1),
                Vector2D(2, 2)
            )
            RotateOrientation.R180 -> setOf(
                Vector2D(0, 0),
                Vector2D(1, 0),
                Vector2D(2, 0),
                Vector2D(3, 0)
            )
            RotateOrientation.R270 -> setOf(
                Vector2D(1, -1),
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(1, 2)
            )
        }
    }

    object O: Mino() {
        override val color = Color.Yellow
//        override fun getRotate(orientation: RotateOrientation) = setOf(
//            Vector2D(1, 1),
//            Vector2D(2, 1),
//            Vector2D(1, 2),
//            Vector2D(2, 2)
//        )
        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
            RotateOrientation.R0 -> setOf(
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(1, 2),
                Vector2D(2, 2)
            )
            RotateOrientation.R90 -> setOf(
                Vector2D(2, 1),
                Vector2D(3, 1),
                Vector2D(2, 2),
                Vector2D(3, 2)
            )
            RotateOrientation.R180 -> setOf(
                Vector2D(2, 0),
                Vector2D(3, 0),
                Vector2D(2, 1),
                Vector2D(3, 1)
            )
            RotateOrientation.R270 -> setOf(
                Vector2D(1, 0),
                Vector2D(2, 0),
                Vector2D(1, 1),
                Vector2D(2, 1)
            )
        }
    }

    object T: Mino() {
        override val color = Color.Purple
        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
            RotateOrientation.R0 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(1, 2)
            )
            RotateOrientation.R90 -> setOf(
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(1, 2),
                Vector2D(2, 1)
            )
            RotateOrientation.R180 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(1, 0)
            )
            RotateOrientation.R270 -> setOf(
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(1, 2),
                Vector2D(0, 1)
            )
        }
    }

    object J: Mino() {
        override val color = Color.Blue
        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
            RotateOrientation.R0 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(0, 2)
            )
            RotateOrientation.R90 -> setOf(
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(1, 2),
                Vector2D(2, 2)
            )
            RotateOrientation.R180 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(2, 0)
            )
            RotateOrientation.R270 -> setOf(
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(1, 2),
                Vector2D(0, 0)
            )
        }
    }

    object L: Mino() {
        override val color = Color.Orange
        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
            RotateOrientation.R0 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(2, 2)
            )
            RotateOrientation.R90 -> setOf(
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(1, 2),
                Vector2D(2, 0)
            )
            RotateOrientation.R180 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(0, 0)
            )
            RotateOrientation.R270 -> setOf(
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(1, 2),
                Vector2D(0, 2)
            )
        }
    }

    object S: Mino() {
        override val color = Color.Green
        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
            RotateOrientation.R0 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(1, 2),
                Vector2D(2, 2)
            )
            RotateOrientation.R90 -> setOf(
                Vector2D(1, 2),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(2, 0)
            )
            RotateOrientation.R180 -> setOf(
                Vector2D(0, 0),
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(2, 1)
            )
            RotateOrientation.R270 -> setOf(
                Vector2D(0, 2),
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(1, 0)
            )
        }
    }

    object Z: Mino() {
        override val color = Color.Red
        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
            RotateOrientation.R0 -> setOf(
                Vector2D(0, 2),
                Vector2D(1, 2),
                Vector2D(1, 1),
                Vector2D(2, 1)
            )
            RotateOrientation.R90 -> setOf(
                Vector2D(1, 0),
                Vector2D(1, 1),
                Vector2D(2, 1),
                Vector2D(2, 2)
            )
            RotateOrientation.R180 -> setOf(
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(1, 0),
                Vector2D(2, 0)
            )
            RotateOrientation.R270 -> setOf(
                Vector2D(0, 0),
                Vector2D(0, 1),
                Vector2D(1, 1),
                Vector2D(1, 2)
            )
        }

//        !!!!! SUPER ROTATE !!!!!
//
//        override fun getRotate(orientation: RotateOrientation) = when(orientation) {
//            RotateOrientation.R0 -> setOf(
//                Vector2D(0, 2),
//                Vector2D(1, 2),
//                Vector2D(1, 1),
//                Vector2D(2, 1)
//            )
//            RotateOrientation.R90 -> setOf(
//                Vector2D(2, 1),
//                Vector2D(2, 2),
//                Vector2D(3, 2),
//                Vector2D(3, 3)
//            )
//            RotateOrientation.R180 -> setOf(
//                Vector2D(2, 1),
//                Vector2D(3, 1),
//                Vector2D(3, 0),
//                Vector2D(4, 0)
//            )
//            RotateOrientation.R270 -> setOf(
//                Vector2D(2, 1),
//                Vector2D(2, 0),
//                Vector2D(1, 0),
//                Vector2D(1, -1)
//            )
//        }
    }
}