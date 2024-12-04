data class Grid<T>(
  val coordinatesToValues: Map<Coordinate, T>,
) {
  val xIndices = run {
    val xCoords = coordinatesToValues.keys.map { it.x }
    xCoords.min()..xCoords.max()
  }

  val yIndices = run {
    val yCoords = coordinatesToValues.keys.map { it.y }
    yCoords.min()..yCoords.max()
  }

  fun get(coordinate: Coordinate): T? {
    return coordinatesToValues[coordinate]
  }

  fun get(x: Int, y: Int): T? {
    return get(Coordinate(x, y))
  }

  fun printGrid() {
    for (x in xIndices) {
      for (y in yIndices) {
        print(get(x, y))
      }
      print("\n")
    }
  }
}
