import kotlin.math.abs

data class Vec2(
  val x: Long,
  val y: Long,
) {
  companion object {
    val ZERO = Vec2(0L, 0L)
    val ONE = Vec2(1L, 1L)
  }

  operator fun plus(other: Vec2): Vec2 {
    return Vec2(x + other.x, y + other.y)
  }

  operator fun plus(other: Direction): Vec2 {
    return this + other.movementVec2
  }

  operator fun times(other: Int): Vec2 {
    return times(other.toLong())
  }

  operator fun times(other: Long): Vec2 {
    return Vec2(this.x * other, this.y * other)
  }

  operator fun div(other: Int): Vec2 {
    return div(other.toLong())
  }

  operator fun div(other: Long): Vec2 {
    return div(Vec2(other, other))
  }

  operator fun div(other: Vec2): Vec2 {
    return Vec2(this.x / other.x, this.y / other.y)
  }

  operator fun minus(other: Vec2): Vec2 {
    return Vec2(x - other.x, y - other.y)
  }

  operator fun rem(other: Vec2): Vec2 {
    return Vec2(x % other.x, y % other.y)
  }

  fun manhattanDistance(other: Vec2): Long {
    return abs(x - other.x) + abs(y - other.y)
  }
}
