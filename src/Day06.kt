import day06.Guard
import day06.TileState
import day06.VisitedInfo

fun main() {

  val debugVisualize = false

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

  fun visualize(map: Grid<TileState>, guard: Guard, visited: HashSet<VisitedInfo> = hashSetOf()) {
    if (!debugVisualize) {
      return
    }
    val xMin = minOf(guard.position.x, 0)
    val xMax = maxOf(guard.position.x, map.xIndices.last)
    val yMin = minOf(guard.position.y, 0)
    val yMax = maxOf(guard.position.y, map.yIndices.last)
    val guardMap = mapOf(guard.position to guard)
    val visitedMap = visited.associateBy { it.position }
    for (y in yMin..yMax) {
      for (x in xMin..xMax) {
        val position = Vec2(x, y)
        val guardAtPosition = guardMap[position]
        val visitedLocation = visitedMap[position]
        if (guardAtPosition != null) {
          print(guardAtPosition.visualizedChar())
        } else if (visitedLocation != null) {
          print(visitedLocation.visualizedChar())
        } else {
          print(map[position]?.toString() ?: " ")
        }
      }
      print("\n")
    }
    print("\n")
  }

  fun getInitialPatrolMapAndFinalGuard(map: Grid<TileState>, guard: Guard): Pair<Grid<TileState>, Guard> {
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

    return map to guard
  }

  fun getLoopingPatrolPath(map: Grid<TileState>, guard: Guard): Pair<Grid<TileState>, Guard>? {
    val visited = hashSetOf<VisitedInfo>()
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

        TileState.BLOCKADE -> {
          val visitedInfo = VisitedInfo(currentGuard.position, currentGuard.direction)
          if (!visited.add(visitedInfo)) {
            visualize(map, currentGuard, visited)
            guard.setInactive()
            return map to currentGuard
          }
          currentGuard.rotate()
        }

        TileState.FREE, TileState.GUARD -> {
          map.coordinatesToValues[currentGuard.position] = TileState.VISITED
          currentGuard.move()
        }

        TileState.VISITED -> {
          val visitedInfo = VisitedInfo(currentGuard.position, currentGuard.direction)
          if (!visited.add(visitedInfo)) {
            visualize(map, currentGuard, visited)
            guard.setInactive()
            return map to currentGuard
          }
          map.coordinatesToValues[currentGuard.position] = TileState.VISITED
          currentGuard.move()
        }
      }
    }

    return null
  }

  fun part1(): Int {
    val (map, guard) = readInput()

    val (finalMap, finalGuard) = getInitialPatrolMapAndFinalGuard(map.deepCopy(), guard)
    visualize(finalMap, finalGuard)
    return finalMap.coordinatesToValues.values.count { it == TileState.VISITED }
  }

  fun part2(): Int {
    val (map, guard) = readInput()
    val (patrolMap, _) = getInitialPatrolMapAndFinalGuard(map.deepCopy(), guard)

    val potentialPositionsToBlockade = patrolMap.coordinatesToValues.entries.filter { (position, tile) ->
      tile == TileState.VISITED && position != guard.position
    }.map { it.key }

    val viableBlockades = potentialPositionsToBlockade.count { potentialPositionToBlockade ->
      val newMap = map.copy(coordinatesToValues = map.coordinatesToValues.map {
        if (it.key == potentialPositionToBlockade) {
          it.key to TileState.BLOCKADE
        } else {
          it.key to it.value
        }
      }.toMap(hashMapOf()))
      getLoopingPatrolPath(newMap, guard) != null
    }

    return viableBlockades
  }

  part1().println()
  part2().println()
}