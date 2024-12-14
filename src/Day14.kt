private data class Robot(
  val position: Vec2,
  val velocity: Vec2,
) {
  fun move(seconds: Long, maxCoords: Vec2): Robot {
    val potentiallyNegativeRestingPosition = ((velocity * seconds) + position) % maxCoords
    // Account for negative positions
    val actualRestingPosition = (potentiallyNegativeRestingPosition + maxCoords) % maxCoords
    return copy(position = actualRestingPosition)
  }
}

fun main() {

  fun visualize(countAtPositions: Map<Vec2, Int>, maxCoords: Vec2, quadrantSplitPoints: Vec2? = null) {
    (0 until maxCoords.y).forEach { y ->
      (0 until maxCoords.x).forEach { x ->
        val toDisplay = if (y == quadrantSplitPoints?.y || x == quadrantSplitPoints?.x) {
          " "
        } else {
          countAtPositions[Vec2(x, y)]?.toString() ?: "."
        }
        print(toDisplay)
      }
      println()
    }
    println()
  }

  fun readInput(): List<Robot> {
    return readInput("Day14").map { line ->
      val parts = line.split(" ")

      val positionPart = parts[0].removePrefix("p=")
      val velocityPart = parts[1].removePrefix("v=")

      val (positionX, positionY) = positionPart.split(",").map { it.toLong() }
      val (velocityX, velocityY) = velocityPart.split(",").map { it.toLong() }

      Robot(Vec2(positionX, positionY), Vec2(velocityX, velocityY))
    }
  }

  fun countQuadrant(robotCountAtPositions: Map<Vec2, Int>, xRange: LongRange, yRange: LongRange): Int {
    return xRange.sumOf { x ->
      yRange.sumOf { y ->
        robotCountAtPositions[Vec2(x, y)] ?: 0
      }
    }
  }

  fun part1(seconds: Long, maxCoords: Vec2): Int {
    val initialRobots = readInput()
    val finalRobots = initialRobots.map { it.move(seconds, maxCoords) }

    val quadrantSplitPoints = (maxCoords - Vec2.ONE) / 2
    val robotCountAtPositions = finalRobots.groupingBy { it.position }.eachCount()
    visualize(robotCountAtPositions, maxCoords, quadrantSplitPoints)
    val leftQuadrantXRange = (0 until quadrantSplitPoints.x)
    val rightQuadrantXRange = ((quadrantSplitPoints.x + 1)..maxCoords.x)
    val topQuadrantYRange = (0 until quadrantSplitPoints.y)
    val bottomQuadrantYRange = ((quadrantSplitPoints.y + 1)..maxCoords.y)

    return countQuadrant(robotCountAtPositions, leftQuadrantXRange, topQuadrantYRange) *
        countQuadrant(robotCountAtPositions, leftQuadrantXRange, bottomQuadrantYRange) *
        countQuadrant(robotCountAtPositions, rightQuadrantXRange, topQuadrantYRange) *
        countQuadrant(robotCountAtPositions, rightQuadrantXRange, bottomQuadrantYRange)

  }

  val maxCoords = Vec2(101, 103)

  part1(100, maxCoords).println()
}