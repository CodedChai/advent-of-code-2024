data class Linen(
  val availablePatterns: List<String>,
  val desiredDesigns: List<String>,
)

fun main() {

  fun readInput(): Linen {
    val input = readInput("Day19")
    val availablePatterns = input.first().split(", ")

    val desiredDesigns = input.drop(2)
    return Linen(availablePatterns, desiredDesigns)
  }

  val cache = hashMapOf<String, Long>()
  fun countValidDesigns(desiredDesign: String, availablePatterns: List<String>): Long {
    if (desiredDesign.isEmpty()) {
      return 1
    }
    return cache.getOrPut(desiredDesign) {
      val validPatterns = availablePatterns.filter { desiredDesign.startsWith(it) }
      validPatterns.sumOf { pattern ->
        val desiredDesignSubstring = desiredDesign.drop(pattern.length)
        countValidDesigns(desiredDesignSubstring, availablePatterns)
      }
    }
  }

  fun part1(): Int {
    val linen = readInput()
    return linen.desiredDesigns.count { desiredDesign ->
      countValidDesigns(desiredDesign, linen.availablePatterns) > 0
    }
  }

  fun part2(): Long {
    val linen = readInput()
    return linen.desiredDesigns.sumOf { desiredDesign ->
      countValidDesigns(desiredDesign, linen.availablePatterns)
    }
  }

  part1().println()
  part2().println()
}