data class Vector2D(
    val x: Int,
    val y: Int
) {
    operator fun unaryMinus(): Vector2D {
        return Vector2D(x*-1, y*-1)
    }

    operator fun plus(other: Vector2D): Vector2D {
        return Vector2D(x + other.x, y + other.y)
    }

    operator fun minus(other: Vector2D): Vector2D {
        return Vector2D(x - other.x, y - other.y)
    }
}

