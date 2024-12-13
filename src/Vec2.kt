data class Vec2(
  val x: Long,
  val y: Long,
) {
  operator fun plus(other: Vec2): Vec2 {
    return Vec2(x + other.x, y + other.y)
  }

  operator fun plus(other: Direction): Vec2 {
    return this + other.movementVec2
  }

  operator fun times(other: Int): Vec2 {
    return Vec2(this.x * other, this.y * other)
  }

  operator fun minus(other: Vec2): Vec2 {
    return Vec2(x - other.x, y - other.y)
  }
}
