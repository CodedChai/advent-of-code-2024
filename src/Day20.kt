import java.util.*
import kotlin.math.abs

private enum class RaceTileType(val display: Char) {
  EMPTY('.'),
  WALL('#'),
  START('S'),
  END('E'),
}

fun main() {

  fun readInput(): Grid<RaceTileType> {
    return readGridInput("Day20").coordinatesToValues.map { (key, value) ->
      key to RaceTileType.entries.first { it.display == value }
    }.toMap(hashMapOf()).let { Grid(it) }
  }

  fun visualizeCheat(raceTrack: Grid<RaceTileType>, currSpot: Vec2, cheatSpot: Vec2) {
    println()
    raceTrack.yIndices.forEach { y ->
      raceTrack.xIndices.forEach { x ->
        val pos = Vec2(x, y)
        val charToPrint = if (pos == currSpot) {
          "O"
        } else if (pos == cheatSpot) {
          if (raceTrack[cheatSpot] == RaceTileType.WALL) {
            "@"
          } else {
            "X"
          }
        } else {
          raceTrack[pos]!!.display
        }
        print(charToPrint)
      }
      println()
    }
  }

  fun getRacePath(
    startPos: Vec2,
    endPos: Vec2,
    maze: Grid<RaceTileType>
  ): HashMap<Vec2, Long> {
    val queue = ArrayDeque<Vec2>()
    queue.add(startPos)
    val visitedToSteps = hashMapOf<Vec2, Long>()
    visitedToSteps[startPos] = 1
    while (queue.isNotEmpty()) {
      val currentPos = queue.removeFirst()
      if (currentPos == endPos) {
        visitedToSteps[currentPos] = visitedToSteps.size + 1L
        break
      }

      // Only one solution so this should only be one thing
      val newPos = Direction.neighbors().firstNotNullOf { neighbor ->
        val potentialNewPos = currentPos + neighbor
        if (maze[potentialNewPos] in setOf(
            RaceTileType.END,
            RaceTileType.EMPTY
          ) && visitedToSteps[potentialNewPos] == null
        ) {
          potentialNewPos
        } else {
          null
        }
      }
      visitedToSteps[newPos] = visitedToSteps.size + 1L
      queue.add(newPos)
    }

    return visitedToSteps
  }

  fun getNeighborsWithinCheatRange(pos: Vec2, distance: Int): List<Vec2> {
    return (-distance..distance).flatMap { distanceX ->
      val remainingDistance = distance - abs(distanceX)
      (-remainingDistance..remainingDistance).map { distanceY ->
        pos + Vec2(distanceX.toLong(), distanceY.toLong())
      }
    }
  }


  fun raceCheatCount(cheatDistance: Int): Int {
    val maze = readInput()
    val startPos = maze.coordinatesToValues.entries.first { it.value == RaceTileType.START }.key
    val endPos = maze.coordinatesToValues.entries.first { it.value == RaceTileType.END }.key

    val visitedToSteps = getRacePath(startPos, endPos, maze)

    val cheatSavings = visitedToSteps.keys.flatMap { pos ->
      getNeighborsWithinCheatRange(pos, cheatDistance).mapNotNull { cheatPos ->
        visitedToSteps[cheatPos]?.let { cheatSteps ->
          val timeUsed = pos.manhattanDistance(cheatPos)
          val res = cheatSteps - visitedToSteps[pos]!! - timeUsed
          if (endPos == cheatPos) {
            res - 1
          } else {
            res
          }
        }
      }
    }

    return cheatSavings.count { it >= 100 }
  }

  raceCheatCount(2).println()
  raceCheatCount(20).println()
}