fun main() {
  // I need the width & height of the grid
  // Map<Char, List<Vec2>> is the input I need to read in
  // From there it's finding the delta vector of each pair in the letter and then using that delta to create the antinodes on each side
  // Antinodes will simply be HashSet<Vec2>
  // I will want a way to visualize this
  
  fun transformToAntennaToNodes(map: Grid<Char>) = map.coordinatesToValues.entries.filter { it.value != '.' }
    .groupBy { it.value }
    .map { (key, value) ->
      key to value.map { it.key }
    }.toMap()

  fun antennaPositionPairs(positions: List<Vec2>): List<Pair<Vec2, Vec2>> {
    val result = mutableListOf<Pair<Vec2, Vec2>>()
    for (i in 0 until positions.size - 1) {
      for (j in i + 1 until positions.size) {
        result.add(Pair(positions[i], positions[j]))
      }
    }
    return result
  }

  fun visualize(map: Grid<Char>, antinodes: Set<Vec2>) {
    for (y in map.yIndices) {
      for (x in map.xIndices) {
        val position = Vec2(x, y)
        val isAntinodeAtPosition = position in antinodes
        if (isAntinodeAtPosition) {
          print('#')
        } else {
          print(map[position]?.toString() ?: " ")
        }
      }
      print("\n")
    }
    print("\n")
  }

  fun part1(): Int {
    val map = readGridInput("Day08")
    val antennaToNodes = transformToAntennaToNodes(map)

    val allAntinodes = antennaToNodes.entries.flatMapTo(hashSetOf()) { (antenna, positions) ->
      // Standardize top to bottom, left to right
      val allPairs = antennaPositionPairs(positions.sortedBy { it.x }.sortedByDescending { it.y })
      allPairs.flatMap { (position1, position2) ->
        // do top left - bottom right
        val deltaVector = position1 - position2
        val antinode1 = position1 + deltaVector
        val antinode2 = position2 - deltaVector
        listOf(antinode1, antinode2).filter { antinode ->
          antinode.x in map.xIndices && antinode.y in map.yIndices
        }
      }
    }

    visualize(map, allAntinodes)
    return allAntinodes.size
  }

  fun createAntinodes(map: Grid<Char>, position: Vec2, deltaVector: Vec2): List<Vec2> {
    val attemptedAntinode = position + deltaVector
    if (attemptedAntinode.x !in map.xIndices || attemptedAntinode.y !in map.yIndices) {
      return emptyList()
    }

    return createAntinodes(map, attemptedAntinode, deltaVector) + attemptedAntinode
  }

  fun part2(): Int {
    val map = readGridInput("Day08")
    val antennaToNodes = transformToAntennaToNodes(map)

    val allAntinodes = antennaToNodes.entries.flatMapTo(hashSetOf()) { (antenna, positions) ->
      // Standardize top to bottom, left to right
      val allPairs = antennaPositionPairs(positions.sortedBy { it.x }.sortedByDescending { it.y })
      allPairs.flatMap { (position1, position2) ->
        // do top left - bottom right
        val deltaVector = position1 - position2
        createAntinodes(map, position1, deltaVector) + createAntinodes(map, position1, deltaVector * -1) + position1
      }
    }

    visualize(map, allAntinodes)
    return allAntinodes.size
  }

  part1().println()
  part2().println()
}