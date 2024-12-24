private enum class Op {
  AND,
  XOR,
  OR,
}

private data class Gate(
  val gate1: String,
  val gate2: String,
  val op: Op,
  val resultName: String,
) {
  fun gates(): Set<String> {
    return setOf(gate1, gate2)
  }

  fun compute(resultsMap: Map<String, Boolean>): Boolean {
    val value1 = resultsMap[gate1]!!
    val value2 = resultsMap[gate2]!!
    return when (op) {
      Op.AND -> value1 and value2
      Op.OR -> value1 or value2
      Op.XOR -> value1 xor value2
    }
  }
}

fun main() {

  fun readInput(): Pair<MutableMap<String, Boolean>, MutableList<Gate>> {
    val input = readInput("Day24")
    val initialResults = input.takeWhile { it.isNotEmpty() }

    val resultsMap = initialResults.associate { line ->
      val split = line.split(": ")
      val value = split[1].toInt() == 1
      split[0] to value
    }

    val gateInput = input.drop(initialResults.size).filter { it.isNotEmpty() }
    val gates = gateInput.map { line ->
      val op = Op.entries.first { it.name in line }
      val split = line.split(" ")
      Gate(
        gate1 = split[0],
        gate2 = split[2],
        op = op,
        resultName = split.last()
      )
    }

    return resultsMap.toMutableMap() to gates.toMutableList()
  }

  fun List<Boolean>.toLong(): Long {
    return fold(0L) { acc, bit ->
      (acc shl 1) or (if (bit) 1 else 0)
    }
  }

  fun part1(): Long {
    val (resultsMap, gates) = readInput()

    while (gates.isNotEmpty()) {
      val gate = gates.first { gate ->
        gate.gate1 in resultsMap.keys && gate.gate2 in resultsMap.keys
      }
      resultsMap[gate.resultName] = gate.compute(resultsMap)
      gates.remove(gate)
    }

    val zBits = resultsMap.filter { it.key.startsWith("z", true) }.toSortedMap().map { it.value }.reversed()
    zBits.println()
    resultsMap.println()
    return zBits.toLong()
  }

  readInput().println()
  part1().println()
}