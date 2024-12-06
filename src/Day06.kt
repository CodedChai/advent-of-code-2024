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


  fun visualize(map: Grid<TileState>, guard: Guard) {
    val xMin = minOf(guard.position.x, 0)
    val xMax = maxOf(guard.position.x, map.xIndices.last)
    val yMin = minOf(guard.position.y, 0)
    val yMax = maxOf(guard.position.y, map.yIndices.last)
    val guardMap = mapOf(guard.position to guard)
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

  fun getInitialPatrolMapAndFinalGuard(mapInput: Grid<TileState>, guard: Guard): Pair<Grid<TileState>, Guard> {
    val map = mapInput.copy()
    var currentGuard = guard
    while (currentGuard.isActive) {
      // Process step
      val positionToMoveTo = currentGuard.getPositionToMoveTo()
      val mapTile = map[positionToMoveTo]
      currentGuard = when (mapTile) {
        null -> {
          map.coordinatesToValues[currentGuard.position] = TileState.VISITED
          currentGuard.setInactive()
        }

        TileState.BLOCKADE -> currentGuard.rotate()
        TileState.FREE, TileState.VISITED, TileState.GUARD -> {
          map.coordinatesToValues[currentGuard.position] = TileState.VISITED
          currentGuard.move()
        }
      }
    }

    return mapInput to guard
  }

  fun part1(): Int {
    val (map, guard) = readInput()

    val (finalMap, finalGuard) = getInitialPatrolMapAndFinalGuard(map, guard)
    visualize(finalMap, finalGuard)
    return finalMap.coordinatesToValues.values.count { it == TileState.VISITED }
  }

  part1().println()
}