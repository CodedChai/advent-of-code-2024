data class Grid<T>(
  val coordinatesToValues: HashMap<Vec2, T>,
) {
  val xIndices = run {
    val xCoords = coordinatesToValues.keys.map { it.x }
    xCoords.min()..xCoords.max()
  }

  val yIndices = run {
    val yCoords = coordinatesToValues.keys.map { it.y }
    yCoords.min()..yCoords.max()
  }

  fun deepCopy(): Grid<T> {
    return this.copy(coordinatesToValues = this.coordinatesToValues.map { it.key to it.value }.toMap(hashMapOf()))
  }

  operator fun get(vec2: Vec2): T? {
    return coordinatesToValues[vec2]
  }

  fun get(x: Long, y: Long): T? {
    return get(Vec2(x, y))
  }

  fun visualize() {
    for (y in yIndices) {
      for (x in xIndices) {
        print(get(x, y).toString())
      }
      print("\n")
    }
  }
}
