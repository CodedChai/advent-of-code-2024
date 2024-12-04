fun main() {
  fun readInput(): Grid<Char> {
    val input = readInput("Day04").flatMapIndexed { yIndex, line ->
      line.mapIndexed { xIndex, char ->
        Coordinate(xIndex, yIndex) to char
      }
    }.toMap()

    return Grid(input)
  }

  data class Check(
    val direction: Direction,
    val word: String,
    val currentCoordinate: Coordinate,
  )

  fun part1Recursion(check: Check, crossword: Grid<Char>): Int {
    if (check.word.isEmpty()) {
      return 1
    }
    val coordToCheck = check.currentCoordinate + check.direction.movementCoordinate
    if (crossword.get(coordToCheck)?.equals(check.word.first(), true) == true) {
      val newCheck = Check(
        direction = check.direction,
        word = check.word.substring(1),
        currentCoordinate = coordToCheck,
      )
      return part1Recursion(newCheck, crossword)
    } else {
      return 0
    }
  }

  fun part1(): Int {
    val crossword = readInput()
    val remainingWord = "MAS"
    var totalFound = 0
    for (x in crossword.xIndices) {
      for (y in crossword.yIndices) {
        if (crossword.get(x, y)?.equals('X', true) == true) {
          totalFound += Direction.entries.sumOf { direction ->
            val check = Check(
              direction,
              remainingWord,
              Coordinate(x, y)
            )
            part1Recursion(check, crossword)
          }
        }
      }
    }

    return totalFound
  }

  fun part2(): Int {
    val crossword = readInput()
    val charsToFind = listOf('M', 'S')
    var totalFound = 0
    for (x in crossword.xIndices) {
      for (y in crossword.yIndices) {
        val currentCoordinate = Coordinate(x, y)
        if (crossword.get(currentCoordinate)?.equals('A', true) == true) {
          val validLeft = listOfNotNull(
            crossword.get(currentCoordinate + Direction.LEFT_UP),
            crossword.get(currentCoordinate + Direction.RIGHT_DOWN),
          ).containsAll(charsToFind)
          val validRight = listOfNotNull(
            crossword.get(currentCoordinate + Direction.RIGHT_UP),
            crossword.get(currentCoordinate + Direction.LEFT_DOWN),
          ).containsAll(charsToFind)
          if (validRight && validLeft) {
            totalFound++
          }
        }
      }
    }

    return totalFound
  }

  println("Part 1 found ${part1()} words")
  println("Part 2 found ${part2()} words")
}