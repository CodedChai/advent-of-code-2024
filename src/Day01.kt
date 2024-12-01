import kotlin.math.abs

fun main() {
  fun loadInLocations(): Pair<List<Int>, List<Int>> {
    val locationList1 = mutableListOf<Int>()
    val locationList2 = mutableListOf<Int>()
    readInput("Day01").map { line ->
      val (location1, location2) = line.split("   ")
      locationList1.add(location1.toInt())
      locationList2.add(location2.toInt())
    }
    return Pair(locationList1, locationList2)
  }

  fun part1(): Int {
    val (locations1, locations2) = loadInLocations()
    val sortedLocations1 = locations1.sorted()
    val sortedLocations2 = locations2.sorted()
    var totalDistanceDelta = 0
    for (i in sortedLocations1.indices) {
      totalDistanceDelta += abs(sortedLocations1[i] - sortedLocations2[i])
    }
    return totalDistanceDelta
  }

  fun part2(): Int {
    val (locations1, locations2) = loadInLocations()
    val locations2Map = locations2.groupBy { it }.map { (key, value) -> key to value.size }.toMap()
    return locations1.sumOf {
      it * (locations2Map[it] ?: 0)
    }
  }

  val part1Answer = part1()
  println("Part 1 total distance delta: $part1Answer")

  val part2Answer = part2()
  println("Part 2 similarity score: $part2Answer")
}
