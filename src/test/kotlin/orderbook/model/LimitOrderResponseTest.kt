package orderbook.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class LimitOrderResponseTest {

    @Test
    fun `should create LimitOrderResponse with correct properties`() {
        val id = UUID.randomUUID().toString()
        val customerOrderId = "customer123"

        val response = LimitOrderResponse(
            id = id,
            customerOrderId = customerOrderId
        )

        // Verify properties
        assertEquals(id, response.id)
        assertEquals(customerOrderId, response.customerOrderId)
    }
}