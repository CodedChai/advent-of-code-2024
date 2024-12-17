import java.util.*
import kotlin.math.abs
import kotlin.system.measureTimeMillis

private enum class MazeTileType(val char: Char) {
  START('S'),
  EMPTY('.'),
  END('E'),
  WALL('#'),
}

private val directionsForRotations = listOf(Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP)

private data class Route(
  val currentPosition: Vec2,
  val visited: Set<Pair<Vec2, Direction>> = emptySet(),
  val currentDirection: Direction = Direction.RIGHT,
  val score: Long = 0,
  val numberTurns: Int = 0,
  val numberMoves: Int = 0,
  val priority: Long = 0,
) {
  fun distance(position: Vec2, goal: Vec2): Long {
    return abs(position.x - goal.x) + abs(position.y - goal.y)
  }

  fun rotate(clockwise: Boolean): Route? {
    val newDirection = directionsForRotations[
      ((directionsForRotations.indexOf(currentDirection) + if (clockwise) 1 else -1) + directionsForRotations.size) % directionsForRotations.size
    ]

    val oldVisited = Pair(currentPosition, currentDirection)
    if (oldVisited in visited) {
      return null
    }
    val newVisited = visited + oldVisited
    return copy(
      currentDirection = newDirection,
      visited = newVisited,
      score = score + 1000,
      numberTurns = numberTurns + 1,
      priority = priority + 1000,
    )
  }

  fun stepForward(endGoal: Vec2): Route? {
    val newPosition = currentPosition + currentDirection
    val oldVisited = Pair(currentPosition, currentDirection)
    if (oldVisited in visited) {
      return null
    }
    val newVisited = visited + oldVisited
    val newScore = score + 1
    val newPriority = newScore + (distance(newPosition, endGoal))
    return copy(
      currentPosition = newPosition,
      visited = newVisited,
      score = newScore,
      numberMoves = numberMoves + 1,
      priority = newPriority
    )
  }
}

fun main() {

  fun readInput(): Grid<MazeTileType> {
    return readGridInput("Day16").coordinatesToValues.entries.associateTo(hashMapOf()) { (key, value) ->
      key to MazeTileType.entries.first { it.char == value }
    }.let { Grid(it) }
  }

  fun visualize(grid: Grid<MazeTileType>, routePositions: Set<Vec2> = emptySet()) {
    for (y in grid.yIndices) {
      for (x in grid.xIndices) {
        if (Vec2(x, y) in routePositions) {
          print("O")
        } else {
          print(grid.get(x, y)?.char)
        }
      }
      print("\n")
    }
    print("\n")
  }

  visualize(readInput())

  fun part1(): Long {
    val maze = readInput()
    val startingPosition = maze.coordinatesToValues.entries.first { it.value == MazeTileType.START }.key
    val endingPosition = maze.coordinatesToValues.entries.first { it.value == MazeTileType.END }.key

    val startingRoute = Route(startingPosition)
    val queue = PriorityQueue<Route>(compareBy { it.priority })
    queue.add(startingRoute)
    val positionToBestScore = mutableMapOf<Pair<Vec2, Direction>, Long>()
    while (queue.isNotEmpty()) {
      val currentRoute = queue.poll()
      if (currentRoute.currentPosition == endingPosition) {
        return currentRoute.score
      }

      val newRoutes =
        listOfNotNull(currentRoute.stepForward(endingPosition), currentRoute.rotate(true), currentRoute.rotate(false))
          .filter { maze[it.currentPosition] != MazeTileType.WALL }
      val routesToAdd = newRoutes.mapNotNull { newRoute ->
        val key = Pair(newRoute.currentPosition, newRoute.currentDirection)
        val currentBest = positionToBestScore[key] ?: Long.MAX_VALUE
        if (newRoute.score > currentBest) {
          null
        } else {
          positionToBestScore[key] = newRoute.score
          newRoute
        }
      }
      queue.addAll(routesToAdd)
    }
    return -1L
  }

  fun part2(): Int {
    val maze = readInput()
    val startingPosition = maze.coordinatesToValues.entries.first { it.value == MazeTileType.START }.key
    val endingPosition = maze.coordinatesToValues.entries.first { it.value == MazeTileType.END }.key

    val startingRoute = Route(startingPosition)
    val queue = PriorityQueue<Route>(compareBy { it.priority })
    queue.add(startingRoute)
    val positionToBestScore = mutableMapOf<Pair<Vec2, Direction>, Long>()
    val bestRoutes = mutableSetOf<Route>()
    while (queue.isNotEmpty()) {
      val currentRoute = queue.poll()
      if (currentRoute.currentPosition == endingPosition) {
        bestRoutes.add(currentRoute)
        continue
      }

      val newRoutes =
        listOfNotNull(currentRoute.stepForward(endingPosition), currentRoute.rotate(true), currentRoute.rotate(false))
          .filter { maze[it.currentPosition] != MazeTileType.WALL }
      val routesToAdd = newRoutes.mapNotNull { newRoute ->
        val key = Pair(newRoute.currentPosition, newRoute.currentDirection)
        val currentBest = positionToBestScore[key] ?: Long.MAX_VALUE
        if (newRoute.score > currentBest) {
          null
        } else {
          positionToBestScore[key] = newRoute.score
          newRoute
        }
      }
      queue.addAll(routesToAdd)
    }

    val visitedPositions = (bestRoutes.flatMap { route -> route.visited.map { it.first } } + endingPosition).toSet()
    visualize(maze, visitedPositions)
    return visitedPositions.size
  }

  measureTimeMillis { part1().println() }.println()
  measureTimeMillis { part2().println() }.println()
}