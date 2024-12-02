import kotlin.math.abs

fun main() {

  data class Report(
    val levels: List<Int>,
  ) {
    fun isGraduallyChanging(): Boolean {
      return levels.windowed(2).all { (first, second) ->
        abs(first - second) in setOf(1, 2, 3)
      }
    }

    fun isOnlyIncreasing(): Boolean {
      return levels.windowed(2).all { (first, second) ->
        first < second
      }
    }

    fun isOnlyDecreasing(): Boolean {
      return levels.windowed(2).all { (first, second) ->
        first > second
      }
    }

    fun isSafe(): Boolean {
      return isGraduallyChanging() && (isOnlyIncreasing() || isOnlyDecreasing())
    }
  }

  fun getReports(): List<Report> {
    return readInput("Day02").map { line ->
      val levels = line.split(" ").map { it.toInt() }
      Report(levels)
    }
  }

  fun part1(): Int {
    val reports = getReports()
    return reports.count { it.isSafe() }
  }

  val part1Answer = part1()
  println("$part1Answer safe reports for part 1")

  fun part2(): Int {
    val reports = getReports()
    return reports.count { report ->
      for (i in report.levels.indices) {
        if (Report(report.levels.toMutableList().apply { removeAt(i) }).isSafe()) {
          return@count true
        }
      }
      false
    }
  }

  val part2Answer = part2()
  println("$part2Answer safe reports for part 2 with error dampener")
}