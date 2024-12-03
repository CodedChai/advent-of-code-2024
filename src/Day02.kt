fun main() {

  data class Report(
    val levels: List<Int>,
  )

  fun Collection<Int>.isWithinExpectedRange(): Boolean {
    return windowed(2).all { (first, second) ->
      first - second in setOf(1, 2, 3)
    }
  }

  fun Collection<Int>.isSafe(): Boolean {
    return isWithinExpectedRange() || reversed().isWithinExpectedRange()
  }

  fun getReports(): List<Report> {
    return readInput("Day02").map { line ->
      val levels = line.split(" ").map { it.toInt() }
      Report(levels)
    }
  }

  fun part1(): Int {
    val reports = getReports()
    return reports.count { it.levels.isSafe() }
  }

  val part1Answer = part1()
  println("$part1Answer safe reports for part 1")

  fun part2(): Int {
    val reports = getReports()
    return reports.count { report ->
      for (i in report.levels.indices) {
        if (report.levels.toMutableList().apply { removeAt(i) }.isSafe()) {
          return@count true
        }
      }
      false
    }
  }

  val part2Answer = part2()
  println("$part2Answer safe reports for part 2 with error dampener")
}