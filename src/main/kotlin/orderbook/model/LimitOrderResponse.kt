package orderbook.model

data class LimitOrderResponse(
    val id: String,                // Generated UUID for order
    val customerOrderId: String    // Passed from request
)