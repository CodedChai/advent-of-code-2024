fun main() {

  fun readInput(): Pair<HashMap<Int, HashSet<Int>>, List<List<Int>>> {
    val dependencies: HashMap<Int, HashSet<Int>> = hashMapOf()
    val pagesToProduce: MutableList<List<Int>> = mutableListOf()
    readInput("Day05").forEach { line ->
      if (line.contains("|")) {
        val (numberWithDep, dep) = line.split("|").map { it.toInt() }
        dependencies[numberWithDep] = dependencies[numberWithDep]?.apply { add(dep) } ?: hashSetOf(dep)
      } else if (line.contains(",")) {
        line.split(",").map { it.toInt() }
          .also { pagesToProduce.add(it) }
      }
    }

    return dependencies to pagesToProduce.map { it }
  }

  fun part1(): Int {
    val (dependencies, pagesToProduce) = readInput()
    val validManuals = pagesToProduce.filter { manual ->
      manual.indices.drop(1).all { index ->
        val page = manual[index]
        val dependenciesForPage = dependencies[page] ?: return@all true
        val potentiallyInvalidNums = manual.filter { it in dependenciesForPage }
        potentiallyInvalidNums.any { it in manual.subList(index, manual.size) }
      }
    }

    return validManuals.sumOf { manual ->
      manual[manual.size / 2]
    }
  }

  part1().println()

}