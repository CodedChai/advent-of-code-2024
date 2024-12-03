fun main() {

  fun readAllInput(): String {
    val input = readInput("Day03")
    return input.joinToString()
  }

  fun part1(): Int {
    val input = readAllInput()
    val mulIdentificationRegex = Regex("""mul\(\d{1,3},\d{1,3}\)""")
    val digitRegex = Regex("""\d{1,3}""")

    val validMuls = mulIdentificationRegex.findAll(input)
    return validMuls.sumOf {
      digitRegex.findAll(it.value).fold(1) { total, matchResult -> total * matchResult.value.toInt() }.toInt()
    }
  }

  println("Part 1: ${part1()}")
}