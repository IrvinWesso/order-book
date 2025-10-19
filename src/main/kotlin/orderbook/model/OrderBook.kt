package orderbook.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant


data class OrderBook(
    @field:JsonProperty("Asks")
    val asks: List<OrderItem>,

    @field:JsonProperty("Bids")
    val bids: List<OrderItem>,

    @field:JsonProperty("LastChange")
    val lastChange: Instant = Instant.now(),

    @field:JsonProperty("SequenceNumber")
    val sequenceNumber: Long
)
