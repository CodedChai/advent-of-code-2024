private data class KeyLockPattern(
  val isKey: Boolean,
  val heights: List<Int>,
)

fun main() {

  fun readInput(): List<KeyLockPattern> {
    return readInput("Day25").filter { it.isNotEmpty() }.windowed(7, step = 7).map { pattern ->
      val isKey = pattern.first().all { it == '.' }
      val patternWithoutIdentifierRow = if (isKey) {
        pattern.dropLast(1)
      } else {
        pattern.drop(1)
      }
      val heightMap = (0..4).map { index ->
        patternWithoutIdentifierRow.count { line -> line[index] == '#' }
      }
      KeyLockPattern(isKey, heightMap)
    }
  }

  fun fits(key: KeyLockPattern, lock: KeyLockPattern): Boolean {
    return key.heights.zip(lock.heights).all { (keyHeight, lockHeight) -> (keyHeight + lockHeight) < 6 }
  }

  fun part1(): Int {
    val (keys, locks) = readInput().partition { it.isKey }

    return locks.sumOf { lock ->
      keys.count { key ->
        println("Key: ${key.heights} lock: ${lock.heights} ${fits(key, lock)}")
        fits(key, lock)
      }
    }
  }

  part1().println()
}