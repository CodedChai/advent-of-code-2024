enum class Direction(val movementCoordinate: Coordinate) {
  RIGHT(Coordinate(1, 0)),
  LEFT(Coordinate(-1, 0)),
  DOWN(Coordinate(0, -1)),
  UP(Coordinate(0, 1)),
  RIGHT_UP(Coordinate(1, 1)),
  RIGHT_DOWN(Coordinate(1, -1)),
  LEFT_UP(Coordinate(-1, 1)),
  LEFT_DOWN(Coordinate(-1, -1)),
}