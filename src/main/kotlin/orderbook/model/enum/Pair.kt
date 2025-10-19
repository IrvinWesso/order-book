package orderbook.model.enum

enum class Pair(val pair: String) {
    BTCZAR("BTCZAR"),
    ETHUSD("ETHUSD"),
    LTCUSD("LTCUSD");

    companion object {
        @JvmStatic
        fun fromString(pair: String): Pair =
            Pair.entries.firstOrNull { it.pair.equals(pair, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unsupported symbol: $pair")
    }
}