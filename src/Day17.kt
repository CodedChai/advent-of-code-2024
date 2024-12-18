import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.math.min
import kotlin.system.measureTimeMillis

private enum class Opcode(val id: Int) {
  ADV(0),
  BXL(1),
  BST(2),
  JNZ(3),
  BXC(4),
  OUT(5),
  BDV(6),
  CDV(7),
}

fun Long.truncateToMaxInt(): Long {
  return min(this, Int.MAX_VALUE.toLong())
}

private data class Computer(
  val registerA: Long, // override set to cap to max int
  val registerB: Long,
  val registerC: Long,
  val program: List<Int>,
  val programPointer: Int = 0,
  val out: List<Int> = emptyList(),
) {
  fun getLiteralOperand(): Int {
    return program[programPointer + 1]
  }

  fun getComboOperand(): Long {
    val literalOperand = getLiteralOperand()
    return when (literalOperand) {
      4 -> registerA
      5 -> registerB
      6 -> registerC
      7 -> error("This shouldn't appear")
      else -> literalOperand.toLong()
    }
  }

  fun incrementPointer(amount: Int = 2): Computer {
    return copy(programPointer = programPointer + 2)
  }

  fun adv(): Computer {
    val newRegisterA = (registerA shr getComboOperand().toInt()).truncateToMaxInt()
    return copy(registerA = newRegisterA).incrementPointer()
  }

  fun bxl(): Computer {
    val newRegisterB = registerB xor getLiteralOperand().toLong()
    return copy(registerB = newRegisterB).incrementPointer()
  }

  fun bst(): Computer {
    val newRegisterB = getComboOperand() % 8
    return copy(registerB = newRegisterB).incrementPointer()
  }

  fun jnz(): Computer {
    return if (registerA == 0L) {
      incrementPointer()
    } else {
      return copy(programPointer = getLiteralOperand())
    }
  }

  fun bxc(): Computer {
    val newRegisterB = registerB xor registerC
    return copy(registerB = newRegisterB).incrementPointer()
  }

  fun out(): Computer {
    val output = getComboOperand() % 8
    return copy(out = out + output.toInt()).incrementPointer()
  }

  fun bdv(): Computer {
    val newRegisterB = (registerA shr getComboOperand().toInt()).truncateToMaxInt()
    return copy(registerB = newRegisterB).incrementPointer()
  }

  fun cdv(): Computer {
    val newRegisterC = (registerA shr getComboOperand().toInt()).truncateToMaxInt()
    return copy(registerC = newRegisterC).incrementPointer()
  }

  fun process(skipAdv: Boolean = false): Computer {
    val currentOp = Opcode.entries.first { it.id == program[programPointer] }
    return when (currentOp) {
      Opcode.ADV -> if (skipAdv) incrementPointer() else adv()
      Opcode.BXL -> bxl()
      Opcode.BST -> bst()
      Opcode.JNZ -> jnz()
      Opcode.BXC -> bxc()
      Opcode.OUT -> out()
      Opcode.BDV -> bdv()
      Opcode.CDV -> cdv()
    }
  }

  fun shouldHalt(): Boolean {
    return programPointer !in program.indices
  }
}

fun main() {

  fun readInput(): Computer {
    val input = readInput("Day17").filter { it.isNotEmpty() }
    val registerA = input[0].split("Register A: ")[1].toLong()
    val registerB = input[1].split("Register B: ")[1].toLong()
    val registerC = input[2].split("Register C: ")[1].toLong()
    val program = input.last().split("Program: ")[1].split(",").map { it.toInt() }

    return Computer(registerA = registerA, registerB = registerB, registerC = registerC, program = program)
  }

  fun part1(): List<Int> {
    var computer = readInput()

    while (!computer.shouldHalt()) {
      computer = computer.process()
    }

    return computer.out
  }

  fun part2(): Int = runBlocking(Dispatchers.Default) {
    val computer = readInput()

    val results = (0..Int.MAX_VALUE).map {
      async {
        var currentComputer = computer.copy(registerA = it.toLong())
        while (!currentComputer.shouldHalt()) {
          if (!currentComputer.out.indices.all { index -> currentComputer.out[index] == currentComputer.program[index] }) {
            break
          }
          if (currentComputer.out.size == currentComputer.program.size) {
            println(it)
            return@async it
          }
          currentComputer = currentComputer.process()
        }
        null
      }
    }

    results.awaitAll().filterNotNull().minOrNull() ?: -1
  }

  /**
   *
   * // For every 8 can I just work backwards?
   * // Then I'd start from the end and have to add it to the beginning each time
   * b = a % 8
   * b = b ^ 7
   * c = (a >> b).truncateMaxInt()
   * a = (a >> 3).truncateMaxInt()
   * b = b ^ 7
   * b = b ^ c
   * out(b % 8)
   * if(a != 0){
   *     pointer = 0
   * } else {
   *     done
   * }
   */

  // 265105790796189
  fun part2Better(previousA: Long, computer: Computer, currentProgramIndex: Int): List<Long>? {
    if (currentProgramIndex < 0) {
      return listOf(previousA)
    }
    println("$previousA - ${computer.program.subList(0, currentProgramIndex)}")
    val programValue = computer.program[currentProgramIndex]
    val answers = (0..7).mapNotNull { currentValueToCheck ->
      var currComputer = computer.copy(
        registerA = (previousA shl 3) + currentValueToCheck.toLong(),
        registerB = 0,
        registerC = 0,
        program = computer.program.dropLast(2)
      )
      while (!currComputer.shouldHalt()) {
        currComputer = currComputer.process(true)
      }
      if (currComputer.out.size == 1 && currComputer.out.first() == programValue) {
        part2Better(currComputer.registerA, computer, currentProgramIndex - 1)
      } else {
        null
      }
//      var a = (previousA shl 3) + currentValueToCheck // TODO: is this right?
//      var b = a % 8
//      val c = a shr b.toInt()
//      // Ignore this one?
//      a = a shr 3
//      b = b xor 7
//      b = b xor c
//      val out = b % 8
//      if (out.toInt() == programValue) {
//        val answer = part2Better(a, program.dropLast(1))
//        if (answer != null) {
//          return answer
//        }
//      }
    }.flatten().ifEmpty { return null }
    return answers
  }

  fun part2Entry(): List<Long>? {
    val computer = readInput().copy(registerA = 0, registerB = 0, registerC = 0)

    return part2Better(0L, computer, computer.program.size - 1)?.sorted()?.onEach { a ->
      var currComputer = computer.copy(registerA = a)
      while (!currComputer.shouldHalt()) {
        currComputer = currComputer.process(true)
      }
      currComputer.out.println()
    }
  }


  part1().joinToString(",").println()
  measureTimeMillis { part2Entry().println() }.println()
}