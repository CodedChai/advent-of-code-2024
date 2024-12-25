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
    val str: String,
    val recursionLevel: Int,
  )

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

  val cache2 = hashMapOf<CacheKey, Long>()

  fun findShortestPathLength(stringToMatch: String, limit: Int, depth: Int): Long {
    val key = CacheKey(stringToMatch, depth)
    val buttons = if (depth == 0) {
      keypad
    } else {
      arrowPad
    }
    return cache2.getOrPut(key) {
      val startingPos = buttons['A']!!
      stringToMatch.fold(Pair(startingPos, 0L)) { (currPos, sum), char ->
        val nextPos = buttons[char]!!
        val paths = findPath(buttons, currPos, char.toString())
          .filter { path ->
            path.asSequence().filter { it is RobotAction.Move }.map { (it as RobotAction.Move).direction }
              .runningFold(currPos) { pos, dir -> pos + dir }.all { it in keypad.values }
          }.map { it.toInputString() }.ifEmpty { listOf("A") }

        val length = if (depth == limit) {
          paths.minOf { it.length }.toLong()
        } else {
          paths.minOf {
            findShortestPathLength(
              it,
              limit,
              depth + 1
            )
          }
        }
        nextPos to (sum + length)
      }.second
    }
  }

  fun getSolution(numArrowKeyRobots: Int): Long = runBlocking(Dispatchers.Default) {
    val input = readInput("Day21")

    input.sumOf { code ->
      val codeValue = code.filter { it.isDigit() }.toLong()
      val len = findShortestPathLength(code, numArrowKeyRobots, 0)
      println("$codeValue x $len = ${codeValue * len}")
      len * codeValue
    }
  }

  getSolution(25).println()
}
