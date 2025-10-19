package orderbook.model

import orderbook.model.enum.Side
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class LimitOrderRequestTest {

    @Test
    fun `should create LimitOrderRequest with correct properties`() {
        val orderId = UUID.randomUUID().toString()
        val limitOrder = LimitOrderRequest(
            side = Side.SELL,
            quantity = BigDecimal("0.5"),
            price = BigDecimal("2000"),
            pair = "BTCZAR",
            customerOrderId = orderId
        )

        // Verify inherited properties
        assertEquals(Side.SELL, limitOrder.side)
        assertEquals(BigDecimal("0.5"), limitOrder.quantity)
        assertEquals(BigDecimal("2000"), limitOrder.price)
        assertEquals(orderId, limitOrder.customerOrderId)

        // Verify own properties
        assertEquals("BTCZAR", limitOrder.pair)
        assertTrue(limitOrder.postOnly)       // default value
        assertEquals("GTC", limitOrder.timeInForce) // default value
        assertFalse(limitOrder.allowMargin)   // default value
        assertFalse(limitOrder.reduceOnly)    // default value
    }

    @Test
    fun `should allow overriding default values`() {
        val limitOrder = LimitOrderRequest(
            side = Side.BUY,
            quantity = BigDecimal("1.0"),
            price = BigDecimal("1500"),
            pair = "ETHZAR",
            customerOrderId = "customId",
            postOnly = false,
            timeInForce = "IOC",
            allowMargin = true,
            reduceOnly = true
        )

        // Verify overridden properties
        assertFalse(limitOrder.postOnly)
        assertEquals("IOC", limitOrder.timeInForce)
        assertTrue(limitOrder.allowMargin)
        assertTrue(limitOrder.reduceOnly)
    }
}