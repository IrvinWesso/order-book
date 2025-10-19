package orderbook.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import orderbook.model.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.example.orderbook.OrderBookApplication
import org.example.orderbook.config.InMemoryUserConfig
import org.example.orderbook.config.SecurityConfig
import org.example.orderbook.controller.OrderController
import org.example.orderbook.service.OrderBookService
import java.io.File

@WebMvcTest(controllers = [OrderController::class]) // Only load OrderController for the test
@ContextConfiguration(classes = [OrderBookApplication::class]) // Load app context
@Import(SecurityConfig::class, InMemoryUserConfig::class) // Include security config
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc // MockMvc for simulating HTTP calls

    @Autowired
    private lateinit var objectMapper: ObjectMapper // Jackson for JSON serialization/deserialization

    @MockBean
    private lateinit var orderBookService: OrderBookService // Mock the service layer

    companion object {
        private const val ORDERBOOK_URL = "/api/BTCZAR/orderbook"
        private const val LIMIT_ORDER_URL = "/api/orders/limit"
        private const val TRADE_HISTORY_URL = "/api/BTCZAR/tradehistory"

        // Helper to load a file from disk
        private fun loadJson(filePath: String) = File(filePath).takeIf { it.exists() }
            ?: throw IllegalArgumentException("File not found: $filePath")
    }

    // Generic helper to parse JSON into an object
    private inline fun <reified T> loadFromJson(filePath: String): T =
        objectMapper.readValue(loadJson(filePath))

    // ----------------- GET ORDER BOOK -----------------
    @Nested
    @WithMockUser(username = "trader1", roles = ["USER"]) // Simulate a logged-in user
    inner class GetOrderBookTests {

        @Test
        fun `should return full order book from JSON file`() {
            // Load the expected order book from JSON
            val orderBook: OrderBook = loadFromJson("src/test/resources/BTCZAR_orderbook.json")
            // Mock the service to return this order book
            given(orderBookService.getOrderBook()).willReturn(orderBook)

            // Perform GET request and check the response
            mockMvc.get(ORDERBOOK_URL) {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() } // Expect HTTP 200
                jsonPath("$.Asks[0].side") { value("sell") } // First ask should be 'sell'
                jsonPath("$.Bids[0].side") { value("buy") }  // First bid should be 'buy'
                jsonPath("$.SequenceNumber") { value(orderBook.sequenceNumber.toInt()) } // Sequence number matches
            }
        }
    }

    // ----------------- POST LIMIT ORDER -----------------
    @Nested
    @WithMockUser(username = "trader1", roles = ["USER"])
    inner class PostLimitOrderTests {

        @Test
        fun `should submit limit order and return response`() {
            // Load request and expected response from JSON files
            val request: LimitOrderRequest = loadFromJson("src/test/resources/BTCZAR_limit_order_request.json")
            val response: LimitOrderResponse = loadFromJson("src/test/resources/BTCZAR_limit_order_response.json")

            // Mock service to return the expected response
            given(orderBookService.submitLimitOrder(request)).willReturn(response)

            // Perform POST request and verify the response
            mockMvc.post(LIMIT_ORDER_URL) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }.andExpect {
                status { isOk() }
                jsonPath("$.id") { value(response.id) } // ID should match
                jsonPath("$.customerOrderId") { value(response.customerOrderId) } // customerOrderId should match
            }
        }
    }

    // ----------------- GET TRADE HISTORY -----------------
    @Nested
    inner class GetTradeHistoryTests {

        @Test
        fun `should return full trade history from JSON file`() {
            // Load expected trade history from JSON
            val tradeHistory: List<Trade> = loadFromJson("src/test/resources/BTCZAR_tradehistory.json")
            // Mock service to return this trade history
            given(orderBookService.getRecentTrades()).willReturn(tradeHistory)

            // Perform GET request and check response
            mockMvc.get(TRADE_HISTORY_URL) {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() } // Expect HTTP 200
                jsonPath("$[0].price") { value(tradeHistory[0].price.toDouble()) } // Price matches first trade
                jsonPath("$[0].takerSide") { value(tradeHistory[0].takerSide.toString().lowercase()) } // Side matches
                jsonPath("$[0].quantity") { value(tradeHistory[0].quantity.toDouble()) } // Quantity matches
            }
        }
    }
}
