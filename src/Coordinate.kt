data class Coordinate(
  val x: Int,
  val y: Int,
) {
  operator fun plus(other: Coordinate): Coordinate {
    return Coordinate(x + other.x, y + other.y)
  }

  operator fun plus(other: Direction): Coordinate {
    return this + other.movementCoordinate
  }
}
