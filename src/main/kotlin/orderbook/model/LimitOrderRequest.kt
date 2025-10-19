package orderbook.model

import orderbook.model.enum.Side
import java.math.BigDecimal

data class LimitOrderRequest(
    override val side: Side,
    override val quantity: BigDecimal,
    override val price: BigDecimal,
    val pair: String,
    override val customerOrderId: String,
    val postOnly: Boolean = true,
    val timeInForce: String = "GTC",
    val allowMargin: Boolean = false,
    val reduceOnly: Boolean = false
) : BaseOrder(side, quantity, price, customerOrderId)
