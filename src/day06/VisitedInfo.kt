package day06

import Direction
import Vec2

data class VisitedInfo(
  val position: Vec2,
  val direction: Direction,
) {
  fun visualizedChar(): Char {
    return when (direction) {
      Direction.RIGHT, Direction.LEFT -> '-'
      Direction.UP, Direction.DOWN -> '|'
      else -> throw RuntimeException("Invalid direction: $direction!")
    }
  }
}