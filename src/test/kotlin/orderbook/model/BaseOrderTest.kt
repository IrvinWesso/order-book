package orderbook.model

import orderbook.model.enum.Side
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class BaseOrderTest {

    @Test
    fun `should create BaseOrder with correct properties`() {
        val orderId = UUID.randomUUID().toString()
        val order = BaseOrder(
            side = Side.BUY,
            quantity = BigDecimal("1.5"),
            price = BigDecimal("1000"),
            customerOrderId = orderId
        )

        // Verify all properties
        assertEquals(Side.BUY, order.side)
        assertEquals(BigDecimal("1.5"), order.quantity)
        assertEquals(BigDecimal("1000"), order.price)
        assertEquals(orderId, order.customerOrderId)
    }
}