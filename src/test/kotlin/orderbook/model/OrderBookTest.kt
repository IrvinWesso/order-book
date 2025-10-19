package orderbook.model

import orderbook.model.enum.Side
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderBookTest {

    @Test
    fun `should create OrderBook with correct properties`() {
        val askOrder = OrderItem(
            side = Side.SELL,
            quantity = BigDecimal("0.1"),
            price = BigDecimal("1000"),
            currencyPair = orderbook.model.enum.Pair.BTCZAR,
            orderCount = 1,
            customerOrderId = UUID.randomUUID().toString()
        )
        val bidOrder = OrderItem(
            side = Side.BUY,
            quantity = BigDecimal("0.2"),
            price = BigDecimal("900"),
            currencyPair = orderbook.model.enum.Pair.BTCZAR,
            orderCount = 1,
            customerOrderId = UUID.randomUUID().toString()
        )

        val sequenceNumber = 12345L
        val orderBook = OrderBook(
            asks = listOf(askOrder),
            bids = listOf(bidOrder),
            sequenceNumber = sequenceNumber
        )

        // Verify properties
        assertEquals(1, orderBook.asks.size)
        assertEquals(askOrder, orderBook.asks[0])
        assertEquals(1, orderBook.bids.size)
        assertEquals(bidOrder, orderBook.bids[0])
        assertEquals(sequenceNumber, orderBook.sequenceNumber)

        // lastChange should have defaulted to a recent Instant
        assertTrue(orderBook.lastChange <= Instant.now())
    }

    @Test
    fun `should allow explicit lastChange`() {
        val lastChange = Instant.parse("2025-10-18T20:00:00Z")
        val orderBook = OrderBook(
            asks = emptyList(),
            bids = emptyList(),
            lastChange = lastChange,
            sequenceNumber = 1L
        )

        assertEquals(lastChange, orderBook.lastChange)
        assertEquals(0, orderBook.asks.size)
        assertEquals(0, orderBook.bids.size)
    }
}