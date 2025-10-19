package orderbook.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import orderbook.model.LimitOrderRequest
import orderbook.model.LimitOrderResponse
import orderbook.model.Trade
import org.example.orderbook.service.OrderBookService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.math.BigDecimal

class OrderBookServiceE2ETest {

    // Configure Jackson ObjectMapper for JSON parsing
    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule()) // 👈 enables proper serialization of Instant, LocalDateTime, etc.
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    private lateinit var service: OrderBookService

    @BeforeEach
    fun setup() {
        // Initialize a fresh instance of the OrderBookService before each test
        service = OrderBookService()
    }

    // Utility function to read JSON test files from resources
    private fun readJson(resourcePath: String): String {
        val uri = javaClass.classLoader.getResource(resourcePath)
            ?: throw IllegalArgumentException("File not found: $resourcePath")
        return Files.readString(Paths.get(uri.toURI()))
    }

    @Test
    fun `E2E - submit BUY and SELL limit orders and verify trades and orderbook`() {
        // --- Load JSON test data for BUY request, expected response, and expected trade history ---
        val buyRequest: LimitOrderRequest =
            mapper.readValue(readJson("BTCZAR_limit_order_request.json"))
        val expectedResponse: LimitOrderResponse =
            mapper.readValue(readJson("BTCZAR_limit_order_response.json"))
        val expectedTradeHistory: List<Trade> =
            mapper.readValue(readJson("BTCZAR_tradehistory.json"))

        // --- Submit BUY limit order and validate response ---
        val buyResponse = service.submitLimitOrder(buyRequest)
        assertNotNull(buyResponse.id, "Response ID must be generated")
        assertEquals(expectedResponse.customerOrderId, buyResponse.customerOrderId)

        // --- Submit SELL order to match the BUY order ---
        val sellRequest = buyRequest.copy(
            side = orderbook.model.enum.Side.SELL,
            price = buyRequest.price,
            quantity = buyRequest.quantity
        )
        val sellResponse = service.submitLimitOrder(sellRequest)
        assertNotNull(sellResponse.id, "SELL response ID must be generated")

        // --- Verify resulting trade history after matching orders ---
        val trades = service.getRecentTrades()
        assertTrue(trades.isNotEmpty(), "Trade history should not be empty")

        // Compare first trade with expected trade details from JSON
        val expectedTrade = expectedTradeHistory.first()
        val actualTrade = trades.first()
        assertEquals(expectedTrade.currencyPair, actualTrade.currencyPair)
        assertEquals(expectedTrade.price, actualTrade.price)
        assertEquals(expectedTrade.quantity, actualTrade.quantity)

        // --- Verify order book integrity after trades ---
        val orderBook = service.getOrderBook()

        // 1️⃣ Ensure bids are sorted in descending order by price
        assertTrue(orderBook.bids.zipWithNext { a, b -> a.price >= b.price }.all { it },
            "Bids must be in descending order")

        // 2️⃣ Ensure asks are sorted in ascending order by price
        assertTrue(orderBook.asks.zipWithNext { a, b -> a.price <= b.price }.all { it },
            "Asks must be in ascending order")

        // 3️⃣ Ensure bids do not cross asks (no bid price higher than lowest ask)
        if (orderBook.bids.isNotEmpty() && orderBook.asks.isNotEmpty()) {
            val highestBid = orderBook.bids.maxOf { it.price.toLong() }
            val lowestAsk = orderBook.asks.minOf { it.price.toLong() }
            assertTrue(highestBid <= lowestAsk, "Highest bid must not exceed lowest ask")
        }

        // --- Additional structural assertions on order book ---
        assertTrue(orderBook.sequenceNumber > 0, "Sequence number must be positive")
        assertNotNull(orderBook.lastChange, "Order book lastChange timestamp must be set")
    }

    @Test
    fun `E2E - handle invalid order gracefully`() {
        // Create an invalid order (quantity = 0) which should trigger validation error
        val invalidRequest = LimitOrderRequest(
            side = orderbook.model.enum.Side.BUY,
            quantity = BigDecimal.ZERO,
            price = BigDecimal("1000"),
            pair = "BTCZAR",
            customerOrderId = "invalid-zero"
        )

        // Submit the invalid order and expect an OrderValidationException
        val exception = assertThrows(orderbook.exception.OrderValidationException::class.java) {
            service.submitLimitOrder(invalidRequest)
        }
        // Verify exception message indicates invalid quantity
        assertTrue(exception.message!!.contains("greater than zero"))
    }
}
