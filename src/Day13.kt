private data class ClawMachine(
  val buttonA: Vec2,
  val buttonB: Vec2,
  val prizeLocation: Vec2,
) {
  fun getIntersectionPoint(): Vec2? {
    // Lines are parallel/equal
    val determinant = buttonA.x * buttonB.y - buttonA.y * buttonB.x
    if (determinant == 0L) {
      return null
    }

    val aPresses = (prizeLocation.x * buttonB.y - prizeLocation.y * buttonB.x) / determinant.toDouble()
    val bPresses = (prizeLocation.x - buttonA.x * aPresses) / buttonB.x.toDouble()
    if (aPresses % 1 != 0.0 || bPresses % 1 != 0.0) {
      return null
    }
    return Vec2(aPresses.toLong(), bPresses.toLong())
  }
}

fun main() {

  fun parseButtonInput(line: String): Vec2 {
    val x = line.split("X+")[1].split(",").first().toLong()
    val y = line.split("Y+")[1].split(",").first().toLong()
    return Vec2(x, y)
  }

  fun parsePrizeInput(line: String, prizeOffset: Long): Vec2 {
    val x = line.split("X=")[1].split(",").first().toLong() + prizeOffset
    val y = line.split("Y=")[1].split(",").first().toLong() + prizeOffset
    return Vec2(x, y)
  }

  fun readInput(prizeOffset: Long): List<ClawMachine> {
    val input = readInput("Day13")
    return input.chunked(4).map { chunk ->
      val buttonA = parseButtonInput(chunk[0])
      val buttonB = parseButtonInput(chunk[1])
      val prize = parsePrizeInput(chunk[2], prizeOffset)
      ClawMachine(buttonA, buttonB, prize)
    }
  }

  fun Vec2.isValidIntersectionPoint(maxPresses: Long): Boolean {
    return x in 0..maxPresses && y in 0..maxPresses
  }

  fun findCost(maxPresses: Long, prizeOffset: Long = 0): Long {
    val validIntersections = readInput(prizeOffset).mapNotNull { clawMachine ->
      clawMachine.getIntersectionPoint()
    }.filter { it.isValidIntersectionPoint(maxPresses) }

    return validIntersections.sumOf { intersection ->
      intersection.x * 3L + intersection.y * 1L
    }
  }

  findCost(100).println()
  // There isn't actually an upper bound on button presses for part 2 but I initially misinterpreted the prompt
  findCost(10000000000000L, 10000000000000L).println()
}