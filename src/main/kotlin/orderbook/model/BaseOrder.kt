package orderbook.model

import orderbook.model.enum.Side
import java.math.BigDecimal

open class BaseOrder(
    open val side: Side,
    open val quantity: BigDecimal,
    open val price: BigDecimal,
    open val customerOrderId: String
)
