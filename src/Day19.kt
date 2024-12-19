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

  val cache = hashMapOf<Pair<String, List<String>>, Boolean>()
  fun isValidDesign(desiredDesign: String, availablePatterns: List<String>): Boolean {
    desiredDesign.ifEmpty { return true }
    return cache.getOrPut(Pair(desiredDesign, availablePatterns)) {
      val validPatterns = availablePatterns.filter { desiredDesign.startsWith(it) }
      validPatterns.ifEmpty { return false }
      validPatterns.any { pattern ->
        val desiredDesignSubstring = desiredDesign.drop(pattern.length)
        isValidDesign(desiredDesignSubstring, availablePatterns)
      }
    }
  }

  fun part1(): Int {
    val linen = readInput()
    return linen.desiredDesigns.count { desiredDesign ->
      isValidDesign(desiredDesign, linen.availablePatterns)
    }
  }

  part1().println()
}