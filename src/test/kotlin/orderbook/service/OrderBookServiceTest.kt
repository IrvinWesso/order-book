package orderbook.service

import orderbook.exception.OrderValidationException
import orderbook.model.LimitOrderRequest
import orderbook.model.OrderBook
import orderbook.model.enum.Side
import org.example.orderbook.service.OrderBookService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderBookServiceTest {

    private lateinit var service: OrderBookService

    @BeforeEach
    fun setup() {
        // Initialize a fresh instance of the OrderBookService before each test
        service = OrderBookService()
    }

    @Test
    fun `getOrderBook should return dummy orders`() {
        // Fetch the current order book
        val orderBook: OrderBook = service.getOrderBook()

        // Verify that dummy asks and bids are loaded correctly
        assertEquals(2, orderBook.asks.size) // dummy asks
        assertEquals(2, orderBook.bids.size) // dummy bids

        // Ensure the lastChange timestamp is set correctly
        assertTrue(orderBook.lastChange <= java.time.Instant.now())
    }

    @Test
    fun `submitLimitOrder should add order when no matching`() {
        // Create a limit order request that does not match any existing orders
        val request = LimitOrderRequest(
            side = Side.BUY,
            quantity = BigDecimal("0.1"),
            price = BigDecimal("1000"),
            pair = "BTCZAR",
            customerOrderId = "cust123"
        )

        // Submit the order to the service
        val response = service.submitLimitOrder(request)

        // Verify the response contains the correct customerOrderId
        assertEquals("cust123", response.customerOrderId)
        // Verify the order ID was generated
        assertNotNull(response.id)

        // Check that the order was added to the bids in the order book
        val orderBook = service.getOrderBook()
        assertTrue(orderBook.bids.any { it.customerOrderId == "cust123" })
    }

    @Test
    fun `submitLimitOrder should match dummy order and create trade`() {
        val service = OrderBookService()

        // Create a SELL order that should match an existing BUY order in dummy orders
        val request = LimitOrderRequest(
            side = Side.SELL,
            quantity = BigDecimal("0.02802465"),
            price = BigDecimal("1875206"), // matches dummy BUY bid
            pair = "BTCZAR",
            customerOrderId = "sell1"
        )

        // Submit the order and capture the response
        val response = service.submitLimitOrder(request)

        // Retrieve recent trades
        val trades = service.getRecentTrades()
        assertTrue(trades.isNotEmpty(), "No trades were created")

        // Find the trade corresponding to our submitted order
        val trade = trades.firstOrNull { it.takerSide == request.side && it.quantity == request.quantity }
        assertNotNull(trade, "Trade for submitted order not found")

        // Validate trade details
        assertEquals(request.quantity, trade!!.quantity, "Trade quantity mismatch")
        assertEquals(request.side, trade.takerSide, "Trade taker side mismatch")
        assertEquals(request.price, trade.price, "Trade price mismatch")

        // Validate response
        assertEquals(request.customerOrderId, response.customerOrderId, "Customer order ID mismatch")
        assertTrue(response.customerOrderId.isNotBlank(), "Order ID should not be blank")
    }

    @Test
    fun `submitLimitOrder should throw IllegalArgumentException for negative quantity`() {
        // Create an invalid order with negative quantity
        val request = LimitOrderRequest(
            side = Side.BUY,
            quantity = BigDecimal("-1"), // invalid quantity
            price = BigDecimal("1000"),
            pair = "BTCZAR",
            customerOrderId = "invalid"
        )

        // Expect an exception when submitting the invalid order
        val exception = assertThrows(OrderValidationException::class.java) {
            service.submitLimitOrder(request)
        }
        // Verify exception message contains expected text
        assertTrue(exception.message!!.contains("greater than zero"))
    }

    @Test
    fun `getRecentTrades should return last 5 trades in descending order`() {
        val service = OrderBookService()

        // Submit 5 BUY orders that will later be matched by SELL orders
        repeat(5) { i ->
            service.submitLimitOrder(
                LimitOrderRequest(
                    side = Side.BUY,
                    quantity = BigDecimal("0.02802465"),
                    price = BigDecimal("1875206"),
                    pair = "BTCZAR",
                    customerOrderId = "buy-$i"
                )
            )
        }

        // Submit 5 SELL orders that match the BUY orders, creating trades
        repeat(5) { i ->
            service.submitLimitOrder(
                LimitOrderRequest(
                    side = Side.SELL,
                    quantity = BigDecimal("0.02802465"),
                    price = BigDecimal("1875206"),
                    pair = "BTCZAR",
                    customerOrderId = "sell-$i"
                )
            )
        }

        // Retrieve the 5 most recent trades
        val recentTrades = service.getRecentTrades()

        // Verify that only 5 trades are returned
        assertEquals(5, recentTrades.size)
        // Ensure trades are sorted by tradedAt in descending order
        assertTrue(recentTrades.zipWithNext { a, b -> a.tradedAt >= b.tradedAt }.all { it })
    }

}
