import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

private data class TestFormula(
  val result: Long,
  val values: List<Long>,
) {
  fun isValid(operators: List<Operator>): Boolean {
    val firstOp = operators.first()
    var currentResult = firstOp.op(values[0], values[1])

    for (i in operators.indices.drop(1)) {
      val op = operators[i]
      currentResult = op.op(currentResult, values[i + 1])
    }

    return result == currentResult
  }
}

private enum class Operator(val op: (Long, Long) -> Long) {
  PLUS({ a, b -> a + b }),
  TIMES({ a, b -> a * b }),
  CONCAT({ a, b -> (a.toString() + b.toString()).toLong() }) // For part 1 simply comment this one out
}

private data class OpsToCheck(
  val ops: List<Operator>,
)

fun main() {
  fun readInput(): List<TestFormula> {
    return readInput("Day07").map { line ->
      val (result, restOfLine) = line.split(":")
      val values = restOfLine.trim().split(" ").map { it.toLong() }
      TestFormula(result.toLong(), values)
    }
  }

  fun part1And2(): Long {
    val formulasToTest = readInput()

    return runBlocking(Dispatchers.Default) {
      formulasToTest.map { testFormula ->
        async {
          val firstOpsToCheck = (0 until testFormula.values.size - 1).map { Operator.entries[0] }
          val visited = hashSetOf<OpsToCheck>()
          val queue = ArrayDeque(listOf(OpsToCheck(firstOpsToCheck)))
          while (queue.isNotEmpty()) {
            val opsToCheck = queue.removeFirst()
            if (!visited.add(opsToCheck)) {
              continue
            }
            if (testFormula.isValid(opsToCheck.ops)) {
              return@async testFormula.result
            }

            val opsToReplace = opsToCheck.ops.mapIndexedNotNull { index, operator ->
              if (operator.ordinal < Operator.entries.size - 1) {
                index to Operator.entries[operator.ordinal + 1]
              } else {
                null
              }
            }

            val moreOpsToCheck = opsToReplace.map { (index, op) ->
              opsToCheck.ops.mapIndexed { oldIndex, oldOp ->
                if (oldIndex == index) {
                  op
                } else {
                  oldOp
                }
              }.let { OpsToCheck(it) }
            }.filter { it !in visited }

            queue.addAll(moreOpsToCheck)
          }
          0L
        }
      }.awaitAll().sum()
    }
  }

  measureTimeMillis { part1And2().println() }.println()
}