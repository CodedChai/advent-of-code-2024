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

private data class Computer(
  val registerA: Long,
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

  fun incrementPointer(): Computer {
    return copy(programPointer = programPointer + 2)
  }

  fun adv(): Computer {
    val newRegisterA = (registerA shr getComboOperand().toInt())
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
      copy(programPointer = getLiteralOperand())
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
    val newRegisterB = (registerA shr getComboOperand().toInt())
    return copy(registerB = newRegisterB).incrementPointer()
  }

  fun cdv(): Computer {
    val newRegisterC = (registerA shr getComboOperand().toInt())
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

  /**
   *
   * // For every 8 can I just work backwards?
   * // Then I'd start from the end and have to add it to the beginning each time
   * b = a % 8
   * b = b ^ 7
   * c = (a >> b)
   * a = (a >> 3)
   * b = b ^ 7
   * b = b ^ c
   * out(b % 8)
   * if(a != 0){
   *     pointer = 0
   * } else {
   *     done
   * }
   */

  fun part2Better(computer: Computer): List<Long> {
    val answers = mutableListOf<Long>()
    val queue = ArrayDeque<Pair<Int, Long>>() // offsetFromEnd to a
    queue.add(1 to 0L)
    while (queue.isNotEmpty()) {
      val (currOffset, currA) = queue.removeFirst()
      if (currOffset > computer.program.size) {
        answers.add(currA)
        continue
      }
      val instructionToMatch = computer.program[computer.program.size - currOffset]
      println(
        "$currA - ${
          computer.program.subList(
            0,
            computer.program.size - currOffset
          )
        } - $currOffset"
      )

      (0..7).forEach { currentValueToCheck ->
        val newA = (currA shl 3) or currentValueToCheck.toLong()
        var currComputer = computer.copy(
          registerA = newA,
        )
        while (!currComputer.shouldHalt()) {
          if (currComputer.out.size > computer.program.size) {
            break
          }
          currComputer = currComputer.process()
        }
        if (currComputer.out.isNotEmpty() && currComputer.out.first() == instructionToMatch) {
          queue.add((currOffset + 1) to newA)
        }
      }
    }

    return answers.also { println("Answers: $it") }
  }

  fun part2Entry(): Long? {
    val computer = readInput().copy(registerA = 0, registerB = 0, registerC = 0)
    val part2BetterAnswers = part2Better(computer).sorted()
    part2BetterAnswers.filter { a ->
      var currComputer = computer.copy(registerA = a)
      while (!currComputer.shouldHalt()) {
        if (currComputer.out.size > currComputer.program.size) {
          break
        }
        currComputer = currComputer.process()
      }
      currComputer.out.println()
      currComputer.out == currComputer.program
    }
    return part2BetterAnswers.minOrNull()
  }

  part1().joinToString(",").println()
  println("Answer: ${part2Entry()}")
}