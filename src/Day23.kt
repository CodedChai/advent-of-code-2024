fun main() {
  fun readInput(): List<Pair<String, String>> {
    return readInput("Day23").filter { it.isNotEmpty() }.map { line ->
      val split = line.split("-")
      Pair(split[0], split[1])
    }
  }

  data class Grouping(
    val computer1: String,
    val computer2: String,
    val computer3: String,
  ) {
    fun isFullyConnected(
      network: Map<String, Set<String>>
    ): Boolean {
      return (computer2 in network[computer1].orEmpty() &&
          computer3 in network[computer1].orEmpty() &&
          computer3 in network[computer2].orEmpty())
    }

    fun setOfComputers(): Set<String> {
      return setOf(computer1, computer2, computer3)
    }

    fun anyComputerStartsWith(startsWith: String): Boolean {
      return setOfComputers().any { it.startsWith(startsWith, true) }
    }
  }


  fun groupingsOfThree(computers: Set<String>): List<Grouping> {
    if (computers.size < 3) {
      return emptyList()
    }
    val combos = mutableListOf<Grouping>()
    val listComputers = computers.toList()

    for (i in 0 until listComputers.size - 2) {
      for (j in i + 1 until listComputers.size - 1) {
        for (k in j + 1 until listComputers.size) {
          combos.add(
            Grouping(
              listComputers[i],
              listComputers[j],
              listComputers[k]
            )
          )
        }
      }
    }

    return combos
  }

  fun part1(): Long {
    val input = readInput()

    val lanNetwork = hashMapOf<String, Set<String>>()
    input.forEach { pair ->
      val computer1 = pair.first
      val computer2 = pair.second
      lanNetwork[computer1] = (lanNetwork[computer1].orEmpty()) + computer2
      lanNetwork[computer2] = (lanNetwork[computer2].orEmpty()) + computer1
    }

    val groupings = lanNetwork.entries.asSequence().flatMap { (mainComputer, connectedComputers) ->
      groupingsOfThree(connectedComputers + mainComputer)
    }.distinctBy { it.setOfComputers() }
      .filter { it.isFullyConnected(lanNetwork) }
      .distinctBy { it.setOfComputers() }
      .filter { it.anyComputerStartsWith("t") }.toList()

//    isFullyConnected("aq", "cg", "yn", lanNetwork).println()

    groupings.println()
    groupings.size.println()
    return -1L
  }

  part1()
}