enum class RotateOrientation {
    R0,
    R90,
    R180,
    R270;
    fun getNext(clockwise: Boolean): RotateOrientation {
        return if(clockwise) {
            when(this) {
                R0 -> R90
                R90 -> R180
                R180 -> R270
                R270 -> R0
            }
        } else {
            when(this) {
                R0 -> R270
                R90 -> R0
                R180 -> R90
                R270 -> R180
            }
        }
    }
}