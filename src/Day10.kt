fun main() {
  val fourDirections = listOf(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)

  fun traverse(grid: Grid<Int>, currentPosition: Vec2, currentHeight: Int): Set<Vec2> {
    val currentGridValue = grid[currentPosition]
    if (currentGridValue != currentHeight) {
      return emptySet()
    } else if (currentHeight == 9) {
      return setOf(currentPosition)
    }
    return fourDirections.flatMap { traverse(grid, currentPosition.plus(it), currentHeight + 1) }.toSet()
  }

  fun part1(): Int {
    val grid = readGridInput("Day10").coordinatesToValues.entries.associateTo(hashMapOf()) { (key, value) ->
      key to value.toString().toInt()
    }.let { Grid(it) }
    val endpointsWeCanHikeTo = grid.yIndices.flatMap { y ->
      grid.xIndices.flatMap { x ->
        traverse(grid, Vec2(x, y), 0)
      }
    }

    return endpointsWeCanHikeTo.size
  }

  fun traversePart2(grid: Grid<Int>, currentPosition: Vec2, currentHeight: Int): List<Vec2> {
    val currentGridValue = grid[currentPosition]
    if (currentGridValue != currentHeight) {
      return emptyList()
    } else if (currentHeight == 9) {
      return listOf(currentPosition)
    }
    return fourDirections.flatMap { traversePart2(grid, currentPosition.plus(it), currentHeight + 1) }
  }

  fun part2(): Int {
    val grid = readGridInput("Day10").coordinatesToValues.entries.associateTo(hashMapOf()) { (key, value) ->
      key to value.toString().toInt()
    }.let { Grid(it) }
    val endpointsWeCanHikeTo = grid.yIndices.flatMap { y ->
      grid.xIndices.flatMap { x ->
        traversePart2(grid, Vec2(x, y), 0)
      }
    }

    return endpointsWeCanHikeTo.size
  }

  part1().println()
  part2().println()
}