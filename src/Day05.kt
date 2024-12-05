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
    val validManuals = pagesToProduce.mapNotNull { manual ->
      var isInvalid = false
      for (i in 1 until manual.size) {
        val page = manual[i]
        val dependenciesForPage = dependencies[page]
        if (dependenciesForPage != null) {
          val potentiallyInvalidNums = manual.filter { it in dependenciesForPage }
          if (potentiallyInvalidNums.all { it in manual.subList(i, manual.size) }) {
            continue
          } else {
            isInvalid = true
          }
        }
      }
      if (isInvalid) {
        null
      } else {
        manual
      }
    }
    return validManuals.sumOf { manual ->
      manual[manual.size / 2]
    }
  }

  part1().println()

}