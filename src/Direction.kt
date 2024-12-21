enum class Direction(val movementVec2: Vec2) {
  RIGHT(Vec2(1, 0)),
  LEFT(Vec2(-1, 0)),
  DOWN(Vec2(0, 1)),
  UP(Vec2(0, -1)),
  RIGHT_UP(Vec2(1, -1)),
  RIGHT_DOWN(Vec2(1, 1)),
  LEFT_UP(Vec2(-1, -1)),
  LEFT_DOWN(Vec2(-1, 1));

  companion object {
    fun neighbors(): List<Direction> {
      return listOf(UP, DOWN, LEFT, RIGHT)
    }
  }
}