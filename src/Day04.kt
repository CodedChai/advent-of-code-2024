fun main() {

  data class Check(
    val direction: Direction,
    val word: String,
    val currentVec2: Vec2,
  )

  fun part1Recursion(check: Check, crossword: Grid<Char>): Int {
    if (check.word.isEmpty()) {
      return 1
    }
    val coordToCheck = check.currentVec2 + check.direction.movementVec2
    if (crossword[coordToCheck]?.equals(check.word.first(), true) == true) {
      val newCheck = Check(
        direction = check.direction,
        word = check.word.substring(1),
        currentVec2 = coordToCheck,
      )
      return part1Recursion(newCheck, crossword)
    } else {
      return 0
    }
  }

  fun part1(): Int {
    val crossword = readGridInput("Day04")
    val remainingWord = "MAS"
    var totalFound = 0
    for (x in crossword.xIndices) {
      for (y in crossword.yIndices) {
        if (crossword.get(x, y)?.equals('X', true) == true) {
          totalFound += Direction.entries.sumOf { direction ->
            val check = Check(
              direction,
              remainingWord,
              Vec2(x, y)
            )
            part1Recursion(check, crossword)
          }
        }
      }
    }

    return totalFound
  }

  fun part1take2(): Int {
    val crossword = readGridInput("Day04")
    crossword.visualize()
    val remainingWord = "MAS"
    var totalFound = 0
    for (x in crossword.xIndices) {
      for (y in crossword.yIndices) {
        if (crossword.get(x, y)?.equals('X', true) == true) {
          totalFound += Direction.entries.count { direction ->
            remainingWord.indices.all { index ->
              val coordToCheck = Vec2(x, y) + direction.movementVec2 * (index + 1)
              crossword.get(coordToCheck)?.equals(remainingWord[index], true) == true
            }
          }
        }
      }
    }

    return totalFound
  }

  fun part2(): Int {
    val crossword = readGridInput("Day04")
    val charsToFind = listOf('M', 'S')
    var totalFound = 0
    for (x in crossword.xIndices) {
      for (y in crossword.yIndices) {
        val currentVec2 = Vec2(x, y)
        if (crossword.get(currentVec2)?.equals('A', true) == true) {
          val validLeft = listOfNotNull(
            crossword.get(currentVec2 + Direction.LEFT_UP),
            crossword.get(currentVec2 + Direction.RIGHT_DOWN),
          ).containsAll(charsToFind)
          val validRight = listOfNotNull(
            crossword.get(currentVec2 + Direction.RIGHT_UP),
            crossword.get(currentVec2 + Direction.LEFT_DOWN),
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
  println("Part 1 take 2 found ${part1take2()} words")
  println("Part 2 found ${part2()} words")
}