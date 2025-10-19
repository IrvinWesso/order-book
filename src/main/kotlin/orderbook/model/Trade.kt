package orderbook.model

import orderbook.model.enum.Side
import orderbook.model.enum.Pair
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class Trade(
    val id: UUID = UUID.randomUUID(),
    val price: BigDecimal,
    val quantity: BigDecimal,
    val currencyPair: Pair,
    val tradedAt: Instant = Instant.now(),
    val takerSide: Side,
    val sequenceId: Long,
    val quoteVolume: BigDecimal = price * quantity
)