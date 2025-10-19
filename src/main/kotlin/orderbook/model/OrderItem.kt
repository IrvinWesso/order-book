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
) : BaseOrder(side, quantity, price, customerOrderId)