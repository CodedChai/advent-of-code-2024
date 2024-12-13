private data class Region(
  val positions: Set<Vec2>,
  val crop: Char,
)

fun main() {

  val directions = listOf(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)

  fun getRegion(startingPosition: Vec2, cropToMatch: Char, farm: Grid<Char>): Region? {
    val visitedCrops = hashSetOf<Vec2>()
    val positionsToCheck = ArrayDeque<Vec2>()
    val region = hashSetOf<Vec2>()
    positionsToCheck.add(startingPosition)
    while (positionsToCheck.isNotEmpty()) {
      val positionToCheck = positionsToCheck.removeFirst()
      val cropAtPosition = farm[positionToCheck]
      if (cropAtPosition != cropToMatch || !visitedCrops.add(positionToCheck)) {
        continue
      }
      region.add(positionToCheck)
      positionsToCheck.addAll(directions.map { direction ->
        positionToCheck + direction
      }.filter { it !in visitedCrops }
      )
    }
    region.ifEmpty { return null }
    return Region(region, cropToMatch)
  }

  fun calculatePerimeter(region: Set<Vec2>): Int {
    return region.sumOf { position ->
      val allNeighbors = directions.map { position + it }
      allNeighbors.count { neighbor -> neighbor !in region }
    }
  }

  fun part1(): Int {
    val farm = readGridInput("Day12")
    val visitedCrops = hashSetOf<Vec2>()
    val regions = farm.coordinatesToValues.entries.mapNotNull { (position, crop) ->
      if (position in visitedCrops) {
        return@mapNotNull null
      }
      getRegion(position, crop, farm)?.also {
        visitedCrops.addAll(it.positions)
      }
    }

    return regions.sumOf { region ->
      val area = region.positions.size
      val perimeter = calculatePerimeter(region.positions)
      println("Region ${region.crop} has area $area and perimeter $perimeter for ${area * perimeter} value")
      area * perimeter
    }
  }

  /**
   * Part 2 sure was something. I wanted to come up with some polygon solution but I didn't know of one.
   * Basically I'm finding all nodes that create a line on each edge of the crop. So for example, I find every
   * position in a region that doesn't have a neighbor with the same crop on the left side. So for the `E` shape
   * example this would be everything on the far left that makes up the tall line. Then I will find the node with the max
   * y position and traverse in the negative y direction one step at a time until I don't find a valid neighbor. That
   * is how I know that I have exhausted the fence along that edge. Then I will keep traversing the nodes on that side
   * in this manner to create all the fences that I possibly can. Then once I've done this for all 4 sides I simply add
   * them up and everything is done.
   *
   * Step 1: Find regions
   * Step 2: Find nodes along an edge for a given direction
   * Step 3: Find how many contiguous lines exist for the nodes along that edge
   * Step 4: Repeat steps 2 & 3 for remaining directions & sum the result
   *
   * Simple visualization of the lines on the outside
   *      _
   *  ___|A|
   * |AAAA|
   *  ----
   */
  fun getStartingPosition(
    positions: Set<Vec2>,
    visitedPositions: Set<Vec2>,
    direction: Direction
  ): Pair<Vec2, Direction> {
    // For anything with an edge on the horizontal we need to traverse the vertical to see how long the line is
    // I will go from max y to min y for traversing & same for the fences in the perpendicular
    // If anything has a distance of more than 1 then I will have to call this function again

    return when (direction) {
      Direction.LEFT, Direction.RIGHT -> {
        positions.filter { it !in visitedPositions }.maxBy { it.y } to Direction.DOWN
      }

      Direction.DOWN, Direction.UP -> {
        positions.filter { it !in visitedPositions }.maxBy { it.x } to Direction.LEFT
      }

      else -> error("Invalid direction for this problem")
    }
  }

  fun findCropsOnEdges(region: Region, direction: Direction): List<Vec2> {
    return region.positions.filter { position ->
      position + direction !in region.positions
    }
  }

  fun calculateNumberOfFences(region: Region, direction: Direction): Int {
    val positions = findCropsOnEdges(region, direction).toSet()
    val visitedPositions = hashSetOf<Vec2>()
    var numberOfFences = 0
    while (visitedPositions.size < positions.size) {
      val (startingPosition, movementDirection) = getStartingPosition(positions, visitedPositions, direction)
      visitedPositions.add(startingPosition)
      numberOfFences++
      var positionToCheck = startingPosition
      while (positionToCheck in positions) {
        visitedPositions.add(positionToCheck)
        positionToCheck += movementDirection
      }
    }

    return numberOfFences
  }

  fun part2(): Int {
    val farm = readGridInput("Day12")
    val visitedCrops = hashSetOf<Vec2>()
    val regions = farm.coordinatesToValues.entries.mapNotNull { (position, crop) ->
      if (position in visitedCrops) {
        return@mapNotNull null
      }
      getRegion(position, crop, farm)?.also {
        visitedCrops.addAll(it.positions)
      }
    }

    return regions.sumOf { region ->
      val area = region.positions.size
      val fences = directions.sumOf { direction ->
        calculateNumberOfFences(region, direction)
      }
      println("Region ${region.crop} has area $area and fences $fences for ${area * fences} value")
      area * fences
    }
  }

  part1().println()
  part2().println()
}