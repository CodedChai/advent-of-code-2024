fun main() {

  fun part1(): Long {
    val merchants = readInput("Day22").filter { it.isNotEmpty() }.map { it.toLong() }

    val results = merchants.map { merchantStartingNumber ->
      (0 until 2000).fold(merchantStartingNumber) { secretNumber, _ ->
        var currentSecretNumber = (secretNumber xor (secretNumber * 64)) % 16777216
        currentSecretNumber = (currentSecretNumber xor (currentSecretNumber / 32)) % 16777216
        ((currentSecretNumber xor (currentSecretNumber * 2048)) % 16777216)
      }
    }

    results.println()
    return results.sum()
  }

  data class MerchantAtTime(
    val delta: Long,
    val currPrice: Long,
  )

  data class DeltaGrouping(
    val deltas: List<Long>,
    val price: Long,
  )


  fun part2(): Long {
    val merchants = readInput("Day22").filter { it.isNotEmpty() }.map { it.toLong() }

    val allMerchantDeltas = merchants.map { merchantStartingNumber ->
      var secretNumber = merchantStartingNumber
      (0 until 2000).map { _ ->
        var currentSecretNumber = (secretNumber xor (secretNumber * 64)) % 16777216
        currentSecretNumber = (currentSecretNumber xor (currentSecretNumber / 32)) % 16777216
        currentSecretNumber = ((currentSecretNumber xor (currentSecretNumber * 2048)) % 16777216)
        val priceDelta = (currentSecretNumber % 10) - (secretNumber % 10)
        secretNumber = currentSecretNumber
        MerchantAtTime(priceDelta, (currentSecretNumber % 10))
      }
    }
    val uniqueGroupingsPerMerchant = allMerchantDeltas.map { merchantDelta ->
      val initialGroupings = merchantDelta.windowed(4).map { deltaGrouping ->
        DeltaGrouping(deltaGrouping.map { it.delta }, deltaGrouping.last().currPrice)
      }

      initialGroupings.groupBy { it.deltas }.map { (key, groupings) ->
        val firstOccurrenceOfPrice = initialGroupings.first { initialGrouping -> initialGrouping.deltas == key }.price
        DeltaGrouping(key, firstOccurrenceOfPrice)
      }.associateBy { it.deltas }
    }

    val allKeys = uniqueGroupingsPerMerchant.flatMapTo(hashSetOf()) { it.keys }

    return allKeys.maxOf { key ->
      uniqueGroupingsPerMerchant.sumOf { merchant ->
        merchant[key]?.price ?: 0
      }
    }
  }
// 1966 & 1964 too high
//  part1().println()
  part2().println()
}