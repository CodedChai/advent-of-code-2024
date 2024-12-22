import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

sealed interface RobotAction {
  fun toDisplay(): String
  data class Move(val direction: Direction) : RobotAction {
    override fun toDisplay(): String {
      return when (direction) {
        Direction.UP -> "^"
        Direction.LEFT -> "<"
        Direction.RIGHT -> ">"
        Direction.DOWN -> "v"
        else -> error("Invalid direction: $direction")
      }
    }
  }

  data object PressButton : RobotAction {
    override fun toDisplay(): String {
      return "A"
    }
  }
}

fun main() {
  val keypad = mapOf(
    '7' to Vec2(0, 0), '8' to Vec2(1, 0), '9' to Vec2(2, 0),
    '4' to Vec2(0, 1), '5' to Vec2(1, 1), '6' to Vec2(2, 1),
    '1' to Vec2(0, 2), '2' to Vec2(1, 2), '3' to Vec2(2, 2),
    '0' to Vec2(1, 3), 'A' to Vec2(2, 3)
  )

  val arrowPad = mapOf(
    '^' to Vec2(1, 0), 'A' to Vec2(2, 0),
    '<' to Vec2(0, 1), 'v' to Vec2(1, 1), '>' to Vec2(2, 1),
  )

  /**
   * if we have from x key to y key length (manh dist???) then we can use that
   *
   */

  fun getDirectionsToMove(currentPos: Vec2, goalPos: Vec2, buttons: Map<Char, Vec2>) =
    Direction.neighbors().mapNotNull { direction ->
      val potentialNewDir = currentPos + direction
      if (potentialNewDir.manhattanDistance(goalPos) >= currentPos.manhattanDistance(goalPos) || potentialNewDir !in buttons.values) {
        null
      } else {
        direction
      }
    }

  fun List<RobotAction>.toInputString(): String {
    return joinToString("") { it.toDisplay() }
  }

  data class CacheKey(
    val recursionLevel: Int,
    val str: String,
  )

  val cache = hashMapOf<CacheKey, Int>()

  fun findPath(
    buttons: Map<Char, Vec2>,
    currentPos: Vec2,
    stringToMatch: String,
  ): List<List<RobotAction>> {
    if (stringToMatch.isEmpty()) {
      return listOf(emptyList())
    }
    val goalPos = buttons[stringToMatch.first()] ?: error("Failed to find button for ${stringToMatch.first()}")
    return if (goalPos == currentPos) {
      findPath(buttons, currentPos, stringToMatch.drop(1)).map { listOf(RobotAction.PressButton) + it }
    } else {
      val directionsToMove = getDirectionsToMove(currentPos, goalPos, buttons)
      directionsToMove.flatMap { dir ->
        findPath(buttons, currentPos + dir, stringToMatch).map {
          listOf(RobotAction.Move(dir)) + it
        }
      }
    }
  }

  // Between button presses you should optimize it so that the same ones are in the same order
  fun findAllPaths(buttons: Map<Char, Vec2>, stringToMatch: String): List<List<RobotAction>> {
    val startPos = buttons['A']!!
    return findPath(buttons, startPos, stringToMatch)
  }


  fun getSolution(numArrowKeyRobots: Int): Long = runBlocking(Dispatchers.Default) {
    val input = readInput("Day21")
    val codeToShortestPath = input.map { code ->
      val keypadRobots = listOf(findAllPaths(keypad, code).minBy { it.size })
      code to keypadRobots.sumOf { keypadRobot ->
        (0 until numArrowKeyRobots).sumOf { recursionLevel ->
          val robotActionString = keypadRobot.toInputString()
          (1 until robotActionString.length).sumOf { index ->
            val oldChar = robotActionString[index - 1]
            val currChar = robotActionString[index]
            val startPos = arrowPad[oldChar]!!
            val key = CacheKey(recursionLevel, "$oldChar$currChar")
            cache.getOrPut(key) {
              findPath(
                arrowPad,
                startPos,
                currChar.toString()
              ).minOf { it.size } // TODO: This isn't working right, we need to consider the counts of the sub robots? Or at least the fact that we have to move the sub robots
            }
          }
        }
      }
    }
    cache.println()
    codeToShortestPath.sumOf { (code, len) ->
      val codeValue = code.filter { it.isDigit() }.toLong()
      println("$codeValue x $len = ${codeValue * len}")
      codeValue * len
    }
  }

  getSolution(2).println()

}
