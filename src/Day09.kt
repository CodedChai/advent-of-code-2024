private sealed interface MemoryBlock

class FreeSpace : MemoryBlock

data class FileSpace(
  val id: Int,
) : MemoryBlock

fun main() {

  fun readInput(): List<MemoryBlock> {
    val line = readInput("Day09").first()
    return line.flatMapIndexed { index, c ->
      val size = c.toString().toInt()
      val isFile = index % 2 == 0
      (0 until size).map {
        if (isFile) {
          FileSpace(id = index / 2)
        } else {
          FreeSpace()
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

  fun part1(): Long {
    val diskSpace = readInput()
    diskSpace.visualize()

    val compactedDisk = mutableListOf<FileSpace>()

    var headIndex = 0
    var tailIndex = diskSpace.size - 1
    val fileCount = diskSpace.count { it is FileSpace }
    // TODO: Better break point
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
    compactedDisk.visualize()
    val checksum = compactedDisk.mapIndexed { index, fileSpace ->
      index * fileSpace.id.toLong()
    }.sum()

    return checksum
  }

  readInput().println()
  part1().println()
}