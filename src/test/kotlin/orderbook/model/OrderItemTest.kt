package orderbook.model

import orderbook.model.enum.Pair
import orderbook.model.enum.Side
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class OrderItemTest {

    @Test
    fun `should create OrderItem with correct properties`() {
        val customerOrderId = "customer123"
        val order = OrderItem(
            side = Side.BUY,
            quantity = BigDecimal("2.5"),
            price = BigDecimal("1000"),
            currencyPair = Pair.BTCZAR,
            customerOrderId = customerOrderId
        )

        // Inherited properties
        assertEquals(Side.BUY, order.side)
        assertEquals(BigDecimal("2.5"), order.quantity)
        assertEquals(BigDecimal("1000"), order.price)
        assertEquals(customerOrderId, order.customerOrderId)

        // Own properties
        assertEquals(Pair.BTCZAR, order.currencyPair)
        assertEquals(1, order.orderCount) // default value
        assertNotNull(order.orderId)      // auto-generated UUID
    }

    @Test
    fun `should allow custom orderCount and orderId`() {
        val customId = UUID.randomUUID().toString()
        val order = OrderItem(
            side = Side.SELL,
            quantity = BigDecimal("1.0"),
            price = BigDecimal("5000"),
            currencyPair = Pair.BTCZAR,
            orderCount = 5,
            orderId = customId,
            customerOrderId = "custId"
        )

        assertEquals(5, order.orderCount)
        assertEquals(customId, order.orderId)
        assertEquals("custId", order.customerOrderId)
    }

    @Test
    fun `should throw exception for negative quantity`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            OrderItem(
                side = Side.BUY,
                quantity = BigDecimal("-1"),
                price = BigDecimal("1000"),
                currencyPair = Pair.BTCZAR
            )
        }
        assertEquals("quantity must be non-negative", exception.message)
    }

    @Test
    fun `should throw exception for negative price`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            OrderItem(
                side = Side.BUY,
                quantity = BigDecimal("1"),
                price = BigDecimal("-100"),
                currencyPair = Pair.BTCZAR
            )
        }
        assertEquals("price must be non-negative", exception.message)
    }

    @Test
    fun `should throw exception for invalid orderCount`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            OrderItem(
                side = Side.BUY,
                quantity = BigDecimal("1"),
                price = BigDecimal("1000"),
                currencyPair = Pair.BTCZAR,
                orderCount = 0
            )
        }
        assertEquals("orderCount must be >= 1", exception.message)
    }
}