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

  fun findPath(buttons: Map<Char, Vec2>, currentPos: Vec2, stringToMatch: String): List<RobotAction> {
    if (stringToMatch.isEmpty()) {
      return emptyList()
    }
    val goalPos = buttons[stringToMatch.first()] ?: error("Failed to find button for ${stringToMatch.first()}")

    return if (goalPos == currentPos) {
      listOf(RobotAction.PressButton) + findPath(buttons, currentPos, stringToMatch.drop(1))
    } else {
      val directionsToMove = getDirectionsToMove(currentPos, goalPos, buttons)
      val possibleInputs = directionsToMove.map { dir -> dir to findPath(buttons, currentPos + dir, stringToMatch) }
      val shortestPath = possibleInputs.minBy { it.second.size }
      listOf(RobotAction.Move(shortestPath.first)) + shortestPath.second
    }
  }

  fun optimize(actions: List<RobotAction>): List<RobotAction> {
    val splitUpActions = mutableListOf<List<RobotAction.Move>>()
    var currentActionList = mutableListOf<RobotAction.Move>()
    for (action in actions) {
      when (action) {
        is RobotAction.PressButton -> {
          splitUpActions.add(currentActionList)
          currentActionList = mutableListOf()
        }

        is RobotAction.Move -> {
          currentActionList.add(action)
        }
      }
    }

    return splitUpActions.map { it.sortedBy { it.direction } }
      .flatMap { it + RobotAction.PressButton }.also {
        println("Unoptimized: ${actions.toInputString()} - Optimized: ${it.toInputString()}")
      }
  }

  // Between button presses you should optimize it so that the same ones are in the same order
  fun findPath(buttons: Map<Char, Vec2>, stringToMatch: String): List<RobotAction> {
    val startPos = buttons['A']!!
    val path = findPath(buttons, startPos, stringToMatch)
    return optimize(path)
  }

  // 110902 is too high
  fun part1(): Long {
    val input = readInput("Day21")
    val codeToActions = input.map { code ->
      val keypadString = findPath(keypad, code).toInputString().also { it.println() }
      val arrowPad1String = findPath(arrowPad, keypadString).toInputString().also { it.println() }
      code to findPath(arrowPad, arrowPad1String).toInputString().also { it.println() }
    }

    codeToActions.forEach { println("${it.first}: ${it.second}") }

    return codeToActions.sumOf { (code, actionSequence) ->
      val codeValue = code.filter { it.isDigit() }.toLong()
      val len = actionSequence.length
      println("$codeValue x $len = ${codeValue * len}")
      codeValue * len
    }
  }

  part1().println()

}
