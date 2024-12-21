import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

  fun part1(): Long = runBlocking(Dispatchers.Default) {
    val input = readInput("Day21")
    val codeToActions = input.map { code ->
      async {
        val optimalPath = findAllPaths(keypad, code).flatMap { keypadActions ->
          val arrowPad1Actions = findAllPaths(arrowPad, keypadActions.toInputString())
          arrowPad1Actions.flatMap { arrowPad1Action ->
            findAllPaths(arrowPad, arrowPad1Action.toInputString())
          }
        }.minBy { it.size }
        code to optimalPath.toInputString().also { it.println() }
      }
    }.awaitAll()

    codeToActions.forEach { println("${it.first}: ${it.second}") }

    codeToActions.sumOf { (code, actionSequence) ->
      val codeValue = code.filter { it.isDigit() }.toLong()
      val len = actionSequence.length
      println("$codeValue x $len = ${codeValue * len}")
      codeValue * len
    }
  }

  part1().println()

}
