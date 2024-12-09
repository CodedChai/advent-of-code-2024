private sealed interface MemoryBlock {
  val size: Int
}

data class FreeSpace(
  override val size: Int,
) : MemoryBlock

data class FileSpace(
  override val size: Int,
  val id: Int,
) : MemoryBlock

fun main() {

  fun readInputPart1(): List<MemoryBlock> {
    val line = readInput("Day09").first()
    return line.flatMapIndexed { index, c ->
      val size = c.toString().toInt()
      val isFile = index % 2 == 0
      (0 until size).map {
        if (isFile) {
          FileSpace(size = size, id = index / 2)
        } else {
          FreeSpace(size = size)
        }
      }
    }
  }

  fun List<MemoryBlock>.visualize() {
    forEach { block ->
      val printChar = when (block) {
        is FileSpace -> block.id.toString()
        else -> '.'
      }
      print(printChar)
    }
    print("\n")
  }

  fun List<MemoryBlock>.explodeList(): List<MemoryBlock> {
    return flatMap { block ->
      (0 until block.size).map { block }
    }
  }

  fun List<MemoryBlock>.visualizePart2() {
    explodeList().visualize()
  }

  fun part1(): Long {
    val diskSpace = readInputPart1()

    val compactedDisk = mutableListOf<FileSpace>()

    var headIndex = 0
    var tailIndex = diskSpace.size - 1
    val fileCount = diskSpace.count { it is FileSpace }

    while (compactedDisk.size < fileCount) {
      val headItem = diskSpace[headIndex]
      when (headItem) {
        is FileSpace -> compactedDisk.add(headItem)
        else -> {
          var tailItem = diskSpace[tailIndex]
          while (tailItem !is FileSpace) {
            tailIndex--
            tailItem = diskSpace[tailIndex]
          }
          compactedDisk.add(tailItem)
          tailIndex--
        }
      }
      headIndex++
    }
    val checksum = compactedDisk.mapIndexed { index, fileSpace ->
      index * fileSpace.id.toLong()
    }.sum()

    return checksum
  }

  fun readInputPart2(): List<MemoryBlock> {
    val line = readInput("Day09").first()
    return line.mapIndexed { index, c ->
      val size = c.toString().toInt()
      val isFile = index % 2 == 0
      if (isFile) {
        FileSpace(size = size, id = index / 2)
      } else {
        FreeSpace(size = size)
      }
    }
  }

  fun part2(): Long {
    val diskSpace = readInputPart2().toMutableList()

    var tailId = diskSpace.maxOf { block ->
      when (block) {
        is FileSpace -> block.id
        else -> -1
      }
    }
    while (tailId > 0) {
      val tailIndex = diskSpace.indexOfLast { block ->
        block is FileSpace && block.id == tailId
      }
      val tailFile = diskSpace[tailIndex]

      val freeSpaceIndex = diskSpace.indexOfFirst { block ->
        block is FreeSpace && block.size >= tailFile.size
      }

      if (freeSpaceIndex > tailIndex || freeSpaceIndex == -1) {
        tailId--
        continue
      }

      val freeSpace = diskSpace[freeSpaceIndex]

      if (freeSpace.size == tailFile.size) {
        diskSpace[freeSpaceIndex] = tailFile
        diskSpace[tailIndex] = freeSpace
      } else if (freeSpace.size > tailFile.size) {
        val freeSpaceRemaining = FreeSpace(size = freeSpace.size - tailFile.size)
        val freeSpaceMoving = FreeSpace(size = tailFile.size)
        diskSpace[freeSpaceIndex] = tailFile
        diskSpace[tailIndex] = freeSpaceMoving
        diskSpace.add(freeSpaceIndex + 1, freeSpaceRemaining)
      }

      tailId--
      // diskSpace.visualizePart2()
    }

    val checksum = diskSpace.explodeList().mapIndexed { index, block ->
      when (block) {
        is FileSpace -> index * block.id.toLong()
        else -> 0
      }
    }.sum()

    return checksum
  }

  part1().println()
  part2().println()
}