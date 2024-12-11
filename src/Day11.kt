import arrow.core.MemoizedDeepRecursiveFunction
import arrow.core.memoize
import kotlin.system.measureTimeMillis

private data class Stone(
  val value: Long,
  val iterationsRemaining: Int,
) {
  fun iterateStone(): List<Stone> {
    return when {
      this.value == 0L -> listOf(Stone(1L, this.iterationsRemaining - 1))
      this.value.toString().length % 2 == 0 -> {
        val stoneString = this.value.toString()
        listOf(
          Stone(stoneString.substring(0, stoneString.length / 2).toLong(), this.iterationsRemaining - 1),
          Stone(
            stoneString.substring(stoneString.length / 2, stoneString.length).toLong(),
            this.iterationsRemaining - 1
          ),
        )
      }

      else -> listOf(Stone(this.value * 2024L, this.iterationsRemaining - 1))
    }
  }
}

fun main() {
  fun readInput(): List<Long> {
    return readInput("Day11").first().split(" ").map { split ->
      split.trim().toLong()
    }
  }

  fun part1(timesToBlink: Int): Int {
    assert(timesToBlink < 35) { "Anything bigger than this will take forever to run, anything over 43 will crash" }
    var stones = readInput()

    repeat(timesToBlink) {
      println("Current iteration: $it")
      stones = stones.flatMap { stone ->
        when {
          stone == 0L -> listOf(1L)
          stone.toString().length % 2 == 0 -> {
            val stoneString = stone.toString()
            listOf(
              stoneString.substring(0, stoneString.length / 2).toLong(),
              stoneString.substring(stoneString.length / 2, stoneString.length).toLong(),
            )
          }

          else -> listOf(stone * 2024L)
        }
      }
    }

    return stones.size
  }

  val stoneCache = mutableMapOf<Stone, Long>() // Stone to final sum value

  fun calculateStone(stone: Stone): Long {
    return stoneCache.getOrPut(stone) {
      if (stone.iterationsRemaining == 0) {
        return 1
      } else {
        stone.iterateStone().sumOf { calculateStone(it) }
      }
    }
  }

  fun part2(timesToBlink: Int): Long {
    val stones = readInput()
    return stones.sumOf { stoneValue ->
      val stone = Stone(stoneValue, timesToBlink)
      calculateStone(stone)
    }
  }

  fun calculateStoneArrowMemoized(stone: Stone): Long {
    return if (stone.iterationsRemaining == 0) {
      return 1
    } else {
      stone.iterateStone().sumOf { calculateStone(it) }
    }
  }

  val arrowMemoized = ::calculateStoneArrowMemoized.memoize()

  val calculateStoneWorker = MemoizedDeepRecursiveFunction<Stone, Long> { stone ->
    if (stone.iterationsRemaining == 0) {
      1L
    } else {
      stone.iterateStone().sumOf { callRecursive(it) }
    }
  }

  fun arrowMemoizedSolution(timesToBlink: Int): Long {
    val stones = readInput()
    return stones.sumOf { stoneValue ->
      val stone = Stone(stoneValue, timesToBlink)
      arrowMemoized(stone)
    }
  }

  fun arrowDeepRecMemoizedSolution(timesToBlink: Int): Long {
    val stones = readInput()
    return stones.sumOf { stoneValue ->
      val stone = Stone(stoneValue, timesToBlink)
      calculateStoneWorker(stone)
    }
  }

  part1(25).println()
  part2(75).println()
  measureTimeMillis { part2(75) }.println()
  measureTimeMillis { arrowMemoizedSolution(75) }.println()
  measureTimeMillis { arrowDeepRecMemoizedSolution(75) }.println()
  println("Arrow memoized: ${arrowMemoizedSolution(75)}")
  println("Arrow deep recursive memoized: ${arrowDeepRecMemoizedSolution(75)}")
}