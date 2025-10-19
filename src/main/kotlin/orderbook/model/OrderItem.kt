package orderbook.model

import orderbook.model.enum.Side
import orderbook.model.enum.Pair

import java.math.BigDecimal
import java.util.UUID

data class OrderItem(
    override val side: Side,
    override var quantity: BigDecimal,
    override val price: BigDecimal,
    val currencyPair: Pair,
    val orderCount: Int = 1,
    val orderId: String = UUID.randomUUID().toString(),
    override val customerOrderId: String = ""
) : BaseOrder(side, quantity, price, customerOrderId) {
    init {
        require(quantity.signum() >= 0) { "quantity must be non-negative" }
        require(price.signum() >= 0) { "price must be non-negative" }
        require(orderCount >= 1) { "orderCount must be >= 1" }
    }
}