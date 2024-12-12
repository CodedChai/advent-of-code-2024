private data class Region(
  val positions: Set<Vec2>,
  val crop: Char,
)

fun main() {

  val part1Directions = listOf(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT)

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
      positionsToCheck.addAll(part1Directions.map { direction ->
        positionToCheck + direction
      }.filter { it !in visitedCrops }
      )
    }
    region.ifEmpty { return null }
    return Region(region, cropToMatch)
  }

  fun calculatePerimeter(region: Set<Vec2>): Int {
    return region.sumOf { position ->
      val allNeighbors = part1Directions.map { position + it }
      allNeighbors.count { neighbor -> neighbor !in region }
    }
  }

  fun part1(): Int {
    val farm = readGridInput("Day12")
    val visitedCrops = hashSetOf<Vec2>()
    val regions = mutableListOf<Region>()
    farm.coordinatesToValues.entries.forEach { (position, crop) ->
      if (position in visitedCrops) {
        return@forEach
      }
      val region = getRegion(position, crop, farm) ?: return@forEach
      visitedCrops.addAll(region.positions)
      regions.add(region)
    }

    return regions.sumOf { region ->
      val area = region.positions.size
      val perimeter = calculatePerimeter(region.positions)
      println("Region ${region.crop} has area $area and perimeter $perimeter for ${area * perimeter} value")
      area * perimeter
    }
  }

  part1().println()

}