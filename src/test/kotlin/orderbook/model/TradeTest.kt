package orderbook.model

import orderbook.model.enum.Pair
import orderbook.model.enum.Side
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class TradeTest {

    @Test
    fun `should create Trade with correct properties`() {
        val trade = Trade(
            price = BigDecimal("2000"),
            quantity = BigDecimal("0.5"),
            currencyPair = Pair.BTCZAR,
            takerSide = Side.BUY,
            sequenceId = 1234567890L
        )

        // Check required properties
        assertEquals(BigDecimal("2000"), trade.price)
        assertEquals(BigDecimal("0.5"), trade.quantity)
        assertEquals(Pair.BTCZAR, trade.currencyPair)
        assertEquals(Side.BUY, trade.takerSide)

        // Check default values
        assertNotNull(trade.id)           // UUID generated automatically
        assertNotNull(trade.tradedAt)     // Instant set automatically
        assertTrue(trade.tradedAt <= Instant.now())
    }

    @Test
    fun `should allow custom id and tradedAt`() {
        val customId = UUID.randomUUID()
        val customTime = Instant.parse("2025-10-18T20:00:00Z")
        val trade = Trade(
            id = customId,
            price = BigDecimal("1500"),
            quantity = BigDecimal("1.0"),
            currencyPair = Pair.BTCZAR,
            tradedAt = customTime,
            takerSide = Side.SELL,
            sequenceId = 1234567890L
        )

        assertEquals(customId, trade.id)
        assertEquals(customTime, trade.tradedAt)
        assertEquals(BigDecimal("1500"), trade.price)
        assertEquals(BigDecimal("1.0"), trade.quantity)
        assertEquals(Side.SELL, trade.takerSide)
    }
}