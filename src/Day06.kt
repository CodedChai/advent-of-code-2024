import day06.Guard
import day06.TileState

fun main() {

  fun readInput(): Pair<Grid<TileState>, Guard> {
    val gridInput = readGridInput("Day06")
    var guard: Guard? = null
    return gridInput.coordinatesToValues.entries.associate { (position, char) ->
      val tileState = TileState.entries.first { it.visualizeChar == char }
      if (tileState == TileState.GUARD) {
        guard = Guard(position)
        position to TileState.FREE
      } else {
        position to tileState
      }
    }.toMap(hashMapOf())
      .let { Grid(it) } to guard!!
  }


  fun visualize(map: Grid<TileState>, guards: List<Guard>) {
    val minGuardXIndex = guards.minOf { it.position.x }
    val maxGuardXIndex = guards.maxOf { it.position.x }
    val minGuardYIndex = guards.minOf { it.position.y }
    val maxGuardYIndex = guards.maxOf { it.position.y }
    val xMin = minOf(minGuardXIndex, 0)
    val xMax = maxOf(maxGuardXIndex, map.xIndices.last)
    val yMin = minOf(minGuardYIndex, 0)
    val yMax = maxOf(maxGuardYIndex, map.yIndices.last)
    val guardMap = guards.associateBy { it.position }
    for (y in yMin..yMax) {
      for (x in xMin..xMax) {
        val position = Vec2(x, y)
        val guardAtPosition = guardMap[position]
        if (guardAtPosition != null) {
          print(guardAtPosition.visualizedChar())
        } else {
          print(map[position]?.toString() ?: " ")
        }
      }
      print("\n")
    }
    print("\n")
  }

  fun part1(): Int {
    val (map, guard) = readInput()
    var guards = listOf(guard)
    while (guards.any { it.isActive }) {
      // Process step
      val newGuards = guards.map { guard ->
        // Process each guard individually
        val positionToMoveTo = guard.getPositionToMoveTo()
        val mapTile = map[positionToMoveTo]
        when (mapTile) {
          null -> {
            map.coordinatesToValues[guard.position] = TileState.VISITED
            guard.setInactive()
          }

          TileState.BLOCKADE -> guard.rotate()
          TileState.FREE, TileState.VISITED, TileState.GUARD -> {
            map.coordinatesToValues[guard.position] = TileState.VISITED
            guard.move()
          }
        }
      }

      guards = newGuards
    }

    visualize(map, guards)
    return map.coordinatesToValues.values.count { it == TileState.VISITED }
  }

  part1().println()
}