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

  fun List<Int>.isValid(
    dependencies: HashMap<Int, HashSet<Int>>,
  ): Boolean {
    return this.indices.drop(1).all { index ->
      val page = this[index]
      val dependenciesForPage = dependencies[page] ?: return@all true
      this.subList(0, index).none { it in dependenciesForPage }
    }
  }

  fun part1(): Int {
    val (dependencies, pagesToProduce) = readInput()
    val validManuals = pagesToProduce.filter { manual ->
      manual.isValid(dependencies)
    }

    return validManuals.sumOf { manual ->
      manual[manual.size / 2]
    }
  }

  fun part2(): Int {
    val (dependencies, pagesToProduce) = readInput()
    val invalidManuals = pagesToProduce.filterNot { manual ->
      manual.isValid(dependencies)
    }

    val correctedManuals = invalidManuals.map { invalidManual ->
      val manual = invalidManual.toMutableList()
      while (!manual.isValid(dependencies)) {
        for (outerIndex in manual.indices.drop(1)) {
          val currentPage = manual[outerIndex]
          val dependenciesForPage = dependencies[currentPage] ?: continue
          innerLoop@ for (innerIndex in 0 until outerIndex) {
            val earlierPage = manual[innerIndex]
            if (earlierPage in dependenciesForPage) {
              manual[innerIndex] = currentPage
              manual[outerIndex] = earlierPage
              break@innerLoop
            }
          }
        }
      }
      manual.toList()
    }
    correctedManuals.map { it.println() }
    return correctedManuals.sumOf { manual ->
      manual[manual.size / 2]
    }
  }

  part1().println()
  part2().println()
}