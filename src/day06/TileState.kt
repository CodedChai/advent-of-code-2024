package day06

enum class TileState(val visualizeChar: Char) {
  FREE('.'),
  BLOCKADE('#'),
  VISITED('X'),
  GUARD('^');


  override fun toString(): String {
    return visualizeChar.toString()
  }
}