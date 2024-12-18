private enum class MemoryTileType(val display: String) {
  SAFE("."),
  CORRUPTED("#"),
}

private data class MemoryTraversal(
  val currentPos: Vec2,
  val stepsTaken: Int = 0,
) {
  fun updatePos(newPos: Vec2): MemoryTraversal {
    return MemoryTraversal(newPos, stepsTaken + 1)
  }
}

fun main() {

  fun readInput(): List<Vec2> {
    return readInput("Day18").map { line ->
      val (x, y) = line.split(",")
      Vec2(x.toLong(), y.toLong())
    }
  }

  val goalPos = Vec2(70, 70)

  fun findLowestNumberOfSteps(grid: Grid<MemoryTileType>): Int {
    val queue = ArrayDeque<MemoryTraversal>()
    val visited = hashSetOf<Vec2>()
    queue.add(MemoryTraversal(Vec2.ZERO))
    while (queue.isNotEmpty()) {
      val current = queue.removeFirst()
      if (current.currentPos == goalPos) {
        return current.stepsTaken
      }
      Direction.neighbors().forEach { neighbor ->
        val newPos = current.currentPos + neighbor
        if (grid[newPos] == MemoryTileType.SAFE && visited.add(newPos)) {
          queue.add(current.updatePos(newPos))
        }
      }
    }
    return -1
  }

  fun part1(): Int {
    val fallingBytes = readInput()
    val grid = (0..goalPos.x).flatMap { x ->
      (0..goalPos.y).map { y ->
        Vec2(x, y) to MemoryTileType.SAFE
      }
    }.toMap(hashMapOf()).let { Grid(it) }

    fallingBytes.take(1024).forEach { fallingByte ->
      grid.coordinatesToValues[fallingByte] = MemoryTileType.CORRUPTED
    }

    return findLowestNumberOfSteps(grid)
  }

  fun part2(): Vec2 {
    val fallingBytes = readInput()
    val gridFromInput = (0..goalPos.x).flatMap { x ->
      (0..goalPos.y).map { y ->
        Vec2(x, y) to MemoryTileType.SAFE
      }
    }.toMap(hashMapOf()).let { Grid(it) }

    var currentTimeIteration = 1
    while (currentTimeIteration < fallingBytes.size) {
      val grid = gridFromInput.deepCopy()
      fallingBytes.take(currentTimeIteration).forEach { fallingByte ->
        grid.coordinatesToValues[fallingByte] = MemoryTileType.CORRUPTED
      }
      if (findLowestNumberOfSteps(grid) == -1) {
        return fallingBytes[currentTimeIteration - 1]
      }
      currentTimeIteration++
    }

    return Vec2.ZERO
  }

  part1().println()
  part2().println()
}