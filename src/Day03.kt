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

  fun part2(): Int {
    val input = readAllInput()
    val mulIdentificationRegex = Regex("""mul\(\d{1,3},\d{1,3}\)""")
    val enableRegex = Regex("""do\(\)""")
    val disableRegex = Regex("""don't\(\)""")
    val digitRegex = Regex("""\d{1,3}""")

    var enabled = true
    var total = 0
    for (i in input.indices) {
      if (enabled) {
        mulIdentificationRegex.matchAt(input, i)?.also {
          val mulResult = digitRegex.findAll(it.value)
            .fold(1) { total, matchResult ->
              total * matchResult.value.toInt()
            }.toInt()
          total += mulResult
        }
        disableRegex.matchAt(input, i)?.also { enabled = false }
      } else {
        enableRegex.matchAt(input, i)?.also { enabled = true }
      }
    }

    return total
  }

  println("Part 1: ${part1()}")
  println("Part 2: ${part2()}")
}