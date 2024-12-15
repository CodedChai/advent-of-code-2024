private enum class TileType(val display: Char) {
  EMPTY('.'),
  WALL('#'),
  ROBOT('@'),
  BOX('O'),
}

fun main() {

  fun charToDir(char: Char): Direction {
    return when (char) {
      '>' -> Direction.RIGHT
      '<' -> Direction.LEFT
      'v' -> Direction.DOWN
      '^' -> Direction.UP
      else -> error("Invalid char: $char")
    }
  }

  fun readInput(): Pair<Grid<TileType>, List<Direction>> {
    // read grid, stop at new line
    val input = readInput("Day15")
    val mapInput = input.takeWhile { line ->
      line.isNotEmpty()
    }
    val grid = mapInput.indices.flatMap { y ->
      val line = mapInput[y]
      line.indices.map { x ->
        Vec2(x.toLong(), y.toLong()) to TileType.entries.first { it.display == line[x] }
      }
    }.toMap(hashMapOf()).let { Grid(it) }
    // split on newline and read until end, it's the directions
    val directions = (mapInput.size until input.size).flatMap { lineIndex ->
      input[lineIndex].map { charToDir(it) }
    }
    return grid to directions
  }

  fun visualize(grid: Grid<TileType>) {
    for (y in grid.yIndices) {
      for (x in grid.xIndices) {
        print(grid.get(x, y)?.display)
      }
      print("\n")
    }
  }

  // can be tailrec I think
  fun tilesToMove(robotPosition: Vec2, grid: Grid<TileType>, direction: Direction): List<Pair<Vec2, TileType>>? {
    var currentPosition = robotPosition
    val tilesToMove = mutableListOf<Pair<Vec2, TileType>>()
    while (true) {
      val currentTile = grid[currentPosition]!!
      when (currentTile) {
        TileType.ROBOT, TileType.BOX -> tilesToMove.add(currentPosition to currentTile)
        TileType.EMPTY -> return tilesToMove
        TileType.WALL -> return null
      }
      currentPosition += direction
    }
  }

  fun part1(): Long {
    val (grid, instructions) = readInput()
    val finalGrid = instructions.fold(grid) { currentGrid, direction ->
      val robotPosition = currentGrid.coordinatesToValues.entries.first { it.value == TileType.ROBOT }.key
      val tilesToMove = tilesToMove(robotPosition, currentGrid, direction) ?: return@fold currentGrid
      val newGrid = currentGrid.deepCopy()
      tilesToMove.forEach { tileToMove ->
        val newPosition = tileToMove.first + direction
        newGrid.coordinatesToValues[newPosition] = tileToMove.second
      }
      newGrid.coordinatesToValues[robotPosition] = TileType.EMPTY
      newGrid
    }

    visualize(finalGrid)
    return finalGrid.coordinatesToValues.entries.filter { it.value == TileType.BOX }.sumOf { it.key.x + it.key.y * 100 }
  }

  part1().println()
}