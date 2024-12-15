private enum class TileType(val display: Char) {
  EMPTY('.'),
  WALL('#'),
  ROBOT('@'),
  BOX('O'),
  BOX_LEFT('['),
  BOX_RIGHT(']'),
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

  fun visualize(grid: Grid<TileType>) {
    for (y in grid.yIndices) {
      for (x in grid.xIndices) {
        print(grid.get(x, y)?.display)
      }
      print("\n")
    }
    print("\n")
  }

  fun readInputPart1(): Pair<Grid<TileType>, List<Direction>> {
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

  fun tilesToMovePart1(
    currentPosition: Vec2,
    grid: Grid<TileType>,
    direction: Direction
  ): List<Pair<Vec2, TileType>>? {
    val currentTile = grid[currentPosition]!!
    return when (currentTile) {
      TileType.ROBOT, TileType.BOX -> {
        tilesToMovePart1(currentPosition + direction, grid, direction)?.let {
          listOf(currentPosition to currentTile) + it
        }
      }

      TileType.EMPTY -> emptyList()
      TileType.WALL -> null
      else -> error("This is used for part1")
    }
  }

  fun part1(): Long {
    val (grid, instructions) = readInputPart1()
    val finalGrid = instructions.fold(grid) { currentGrid, direction ->
      val robotPosition = currentGrid.coordinatesToValues.entries.first { it.value == TileType.ROBOT }.key
      val tilesToMove = tilesToMovePart1(robotPosition, currentGrid, direction) ?: return@fold currentGrid
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

  fun transformForPart2(char: Char): String {
    return when (char) {
      TileType.ROBOT.display -> "${TileType.ROBOT.display}${TileType.EMPTY.display}"
      TileType.BOX.display -> "${TileType.BOX_LEFT.display}${TileType.BOX_RIGHT.display}"
      TileType.WALL.display -> "${TileType.WALL.display}${TileType.WALL.display}"
      TileType.EMPTY.display -> "${TileType.EMPTY.display}${TileType.EMPTY.display}"
      else -> error("$char is unaccounted for")
    }
  }

  fun readInputPart2(): Pair<Grid<TileType>, List<Direction>> {
    // read grid, stop at new line
    val input = readInput("Day15")
    val mapInput = input.takeWhile { line ->
      line.isNotEmpty()
    }.map { line ->
      line.map { char ->
        transformForPart2(char)
      }.joinToString("")
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

  fun tilesToMovePart2(
    currentPosition: Vec2,
    grid: Grid<TileType>,
    direction: Direction
  ): List<Pair<Vec2, TileType>>? {
    val currentTile = grid[currentPosition]!!
    return when (currentTile) {
      TileType.ROBOT -> {
        tilesToMovePart2(currentPosition + direction, grid, direction)?.let {
          listOf(currentPosition to currentTile) + it
        }
      }

      TileType.BOX_LEFT -> {
        when (direction) {
          Direction.UP, Direction.DOWN -> {
            // add right side, right side up as recursive and up as recursive
            val right = tilesToMovePart2(currentPosition + direction + Direction.RIGHT, grid, direction)
            val line = tilesToMovePart2(currentPosition + direction, grid, direction)
            if (right != null && line != null) {
              listOf(
                currentPosition to currentTile,
                currentPosition + Direction.RIGHT to TileType.BOX_RIGHT
              ) + line + right
            } else {
              null
            }
          }

          else -> tilesToMovePart2(currentPosition + direction, grid, direction)?.let {
            listOf(currentPosition to currentTile) + it
          }
        }
      }

      TileType.BOX_RIGHT -> {
        when (direction) {
          Direction.UP, Direction.DOWN -> {
            // add right side, right side up as recursive and up as recursive
            val left = tilesToMovePart2(currentPosition + direction + Direction.LEFT, grid, direction)
            val line = tilesToMovePart2(currentPosition + direction, grid, direction)
            if (left != null && line != null) {
              listOf(
                currentPosition to currentTile,
                currentPosition + Direction.LEFT to TileType.BOX_LEFT
              ) + line + left
            } else {
              null
            }
          }

          else -> tilesToMovePart2(currentPosition + direction, grid, direction)?.let {
            listOf(currentPosition to currentTile) + it
          }
        }
      }

      TileType.EMPTY -> emptyList()
      TileType.WALL -> null
      else -> error("This is used for part1")
    }
  }

  fun part2(): Long {
    val (grid, instructions) = readInputPart2()
    val finalGrid = instructions.fold(grid) { currentGrid, direction ->
      val robotPosition = currentGrid.coordinatesToValues.entries.first { it.value == TileType.ROBOT }.key
      val tilesToMove = tilesToMovePart2(robotPosition, currentGrid, direction) ?: return@fold currentGrid
      val newGrid = currentGrid.deepCopy()
      // Blank out all old positions, then add in all of the new ones
      tilesToMove.onEach { tileToMove ->
        newGrid.coordinatesToValues[tileToMove.first] = TileType.EMPTY
      }.onEach { tileToMove ->
        val newPosition = tileToMove.first + direction
        newGrid.coordinatesToValues[newPosition] = tileToMove.second
      }
      newGrid
    }

    visualize(finalGrid)
    return finalGrid.coordinatesToValues.entries.filter { it.value == TileType.BOX_LEFT }
      .sumOf { it.key.x + it.key.y * 100 }
  }

  part1().println()
  part2().println()
}