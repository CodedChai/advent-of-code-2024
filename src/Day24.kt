private enum class Op {
  AND,
  XOR,
  OR,
}

private data class Gate(
  val wire1: String,
  val wire2: String,
  val op: Op,
  val resultName: String,
) {
  fun wires(): Set<String> {
    return setOf(wire1, wire2)
  }

  fun compute(resultsMap: Map<String, Boolean>): Boolean {
    val value1 = resultsMap[wire1]!!
    val value2 = resultsMap[wire2]!!
    return when (op) {
      Op.AND -> value1 and value2
      Op.OR -> value1 or value2
      Op.XOR -> value1 xor value2
    }
  }
}

fun main() {

  fun readInput(): Pair<Map<String, Boolean>, List<Gate>> {
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
        wire1 = split[0],
        wire2 = split[2],
        op = op,
        resultName = split.last()
      )
    }

    return resultsMap to gates
  }

  fun List<Boolean>.toLong(): Long {
    return fold(0L) { acc, bit ->
      (acc shl 1) or (if (bit) 1 else 0)
    }
  }

  fun Long.toBooleanList(): List<Boolean> {
    if (this == 0L) return listOf(false)
    val bits = mutableListOf<Boolean>()
    var tmp = this
    while (tmp > 0) {
      bits.add(0, (tmp and 1) == 1L)
      tmp = tmp shr 1
    }
    return bits
  }

  fun computeResult(resultsMap: Map<String, Boolean>, gates: List<Gate>): List<Boolean> {
    val mutableResultsMap = resultsMap.toMutableMap()
    val mutableGates = gates.toMutableList()
    while (mutableGates.isNotEmpty()) {
      val gate = mutableGates.first { gate ->
        gate.wire1 in mutableResultsMap.keys && gate.wire2 in mutableResultsMap.keys
      }
      mutableResultsMap[gate.resultName] = gate.compute(mutableResultsMap)
      mutableGates.remove(gate)
    }

    return mutableResultsMap.filter { it.key.startsWith("z", true) }.toSortedMap().map { it.value }.reversed()
  }

  fun part1(): Long {
    val (resultsMap, gates) = readInput()
    return computeResult(resultsMap, gates).toLong()
  }

  fun calculateDifference(expectedResult: List<Boolean>, result: List<Boolean>): Int {
    return expectedResult.zip(result).count { (expected, res) ->
      expected != res
    }
  }

  fun visualize() {
    val (_, gates) = readInput()
    val z = gates.filter { it.resultName.startsWith("z") }.map { it.resultName }.sorted().joinToString("->")
    val x = z.replace('z', 'x')
    val y = z.replace('z', 'y')

    println(
      """
        digraph G {
            subgraph {
               node [style=filled,color=green]
                $z
            }
            subgraph {
                node [style=filled,color=gray]
                $x
            }
            subgraph {
                node [style=filled,color=gray]
                $y
            }
            subgraph {
                node [style=filled,color=pink]
                ${gates.filter { gate -> gate.op == Op.AND }.joinToString(" ") { gate -> gate.resultName }}
            }
            subgraph {
                node [style=filled,color=yellow];
                ${gates.filter { gate -> gate.op == Op.OR }.joinToString(" ") { gate -> gate.resultName }}
            }
            subgraph {
                node [style=filled,color=lightblue];
                ${gates.filter { gate -> gate.op == Op.XOR }.joinToString(" ") { gate -> gate.resultName }}
            }
            """.trimIndent()
    )
    gates.forEach { (left, right, _, out) ->
      println("    $left -> $out")
      println("    $right -> $out")
    }
    println("}")
  }

  fun part2(): Long {
    val (resultsMap, gates) = readInput()

    val expectedX =
      resultsMap.filter { it.key.startsWith("x", true) }.toSortedMap().map { it.value }.reversed().toLong()
    val expectedY =
      resultsMap.filter { it.key.startsWith("y", true) }.toSortedMap().map { it.value }.reversed().toLong()
    val expectedResult = (expectedX + expectedY)
    val result = computeResult(resultsMap, gates)

    calculateDifference((expectedX + expectedY).toBooleanList(), result).println()
    println("Diff: " + (expectedResult - result.toLong()))
    return (expectedResult - result.toLong())
  }

  part1().println()
  visualize()
  part2().println()

}