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

fun Double.truncateToMaxInt(): Long {
  return this.toLong().truncateToMaxInt()
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

  fun process(): Computer {
    val currentOp = Opcode.entries.first { it.id == program[programPointer] }
    return when (currentOp) {
      Opcode.ADV -> adv()
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

  part1().joinToString(",").println()
  measureTimeMillis { part2().println() }.println()
}