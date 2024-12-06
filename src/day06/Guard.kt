package day06

import Direction
import Vec2

data class Guard(
  val position: Vec2,
  val direction: Direction = Direction.UP,
  val isActive: Boolean = true,
  val steps: Int = 0,
) {
  fun getPositionToMoveTo(): Vec2 {
    // Our grid's y coordinates are actually complete inverted
    return position + direction.movementVec2.copy(y = direction.movementVec2.y * -1)
  }

  fun setInactive(): Guard {
    return move().copy(isActive = false)
  }

  fun move(): Guard {
    return copy(position = getPositionToMoveTo(), steps = steps + 1)
  }

  fun rotate(): Guard {
    val newDirection = when (direction) {
      Direction.UP -> Direction.RIGHT
      Direction.RIGHT -> Direction.DOWN
      Direction.DOWN -> Direction.LEFT
      Direction.LEFT -> Direction.UP
      else -> throw RuntimeException("Invalid direction $direction!!!!")
    }

    return copy(direction = newDirection, steps = steps + 1)
  }

  fun visualizedChar(): Char {
    return when (direction) {
      Direction.UP -> '^'
      Direction.RIGHT -> '>'
      Direction.DOWN -> 'v'
      Direction.LEFT -> '<'
      else -> throw RuntimeException("Invalid direction $direction!!!!")
    }
  }
}
