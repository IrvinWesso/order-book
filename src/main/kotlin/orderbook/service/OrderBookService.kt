package org.example.orderbook.service

import orderbook.exception.OrderValidationException
import orderbook.model.*
import orderbook.model.enum.Side
import orderbook.model.enum.Pair
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Service class responsible for managing an in-memory order book.
 * Handles:
 *  - Order submission
 *  - Order matching
 *  - Maintaining open orders (bids/asks)
 *  - Tracking recent trades
 */
@Service
class OrderBookService {

    private val logger = LoggerFactory.getLogger(OrderBookService::class.java)

    // Orders grouped by Side (BUY/SELL) using thread-safe collections
    private val orders = ConcurrentHashMap(
        mapOf(
            Side.BUY to CopyOnWriteArrayList<OrderItem>(),
            Side.SELL to CopyOnWriteArrayList<OrderItem>()
        )
    )

    // Simulated exchange sequence ID for trades
    private var sequenceCounter: Long = 1370000000002671000L

    // List to store executed trades
    private val trades = CopyOnWriteArrayList<Trade>()

    // Dummy orders to initialize the order book for testing/demo purposes
    private val dummyOrderBook = OrderBook(
        asks = listOf(
            OrderItem(Side.SELL, BigDecimal("0.075"), BigDecimal("1878404"), Pair.BTCZAR, 1),
            OrderItem(Side.SELL, BigDecimal("0.14071953"), BigDecimal("1878405"), Pair.BTCZAR, 1)
        ),
        bids = listOf(
            OrderItem(Side.BUY, BigDecimal("0.196"), BigDecimal("1875206"), Pair.BTCZAR, 1),
            OrderItem(Side.BUY, BigDecimal("0.02802465"), BigDecimal("1875206"), Pair.BTCZAR, 1)
        ),
        sequenceNumber = 18515253074
    )

    init {
        // Populate the initial orders into the in-memory book
        dummyOrderBook.bids.forEach { orders[Side.BUY]!!.add(it.copy()) } // Note to self: !! means If it is null at runtime, throw a KotlinNullPointerException.
        dummyOrderBook.asks.forEach { orders[Side.SELL]!!.add(it.copy()) }
        logger.info("Initialized OrderBookService with dummy orders: ${getOrderBook()}")
    }

    /**
     * Returns a snapshot of the current order book
     */
    fun getOrderBook(): OrderBook = OrderBook(
        bids = orders[Side.BUY]!!.toList(),  // Copy of current bids
        asks = orders[Side.SELL]!!.toList(), // Copy of current asks
        lastChange = Instant.now(),          // Timestamp of snapshot
        sequenceNumber = dummyOrderBook.sequenceNumber + 1
    ).also { logger.debug("Retrieved order book: $it") }

    /**
     * Returns the 5 most recent trades
     */
    fun getRecentTrades(): List<Trade> =
        trades.sortedByDescending { it.tradedAt }  // Most recent first
            .take(5)                             // Limit to last 5
            .also { logger.debug("Retrieved recent trades: $it") }

    /**
     * Submits a new limit order.
     * Validates order, attempts to match it with opposite side, and adds remaining quantity to book.
     */
    fun submitLimitOrder(limitOrder: LimitOrderRequest): LimitOrderResponse {
        logger.info("Submitting limit order: $limitOrder")

        // Convert incoming request into internal OrderItem
        val order = mapLimitOrderToOrderItem(limitOrder)

        // Validate order fields
        validateOrder(order)

        // Attempt to match the order with existing opposite orders
        matchOrder(order)

        return LimitOrderResponse(order.orderId, order.customerOrderId)
            .also { logger.info("Limit order submitted: $it") }
    }

    /**
     * Matches an incoming order with opposite side orders.
     * Fully or partially fills orders depending on price/quantity.
     * Any remaining quantity is added to the same side of the order book.
     */
    private fun matchOrder(order: OrderItem) {
        val oppositeSide = if (order.side == Side.BUY) Side.SELL else Side.BUY
        val sameSide = order.side
        var remainingQty = order.quantity

        // Iterate over a snapshot to avoid modification during iteration
        orders[oppositeSide]!!.toList().forEach { oppositeOrder ->
            if (remainingQty <= BigDecimal.ZERO) return@forEach // stop this iteration

            // Check if prices allow a match
            val isMatch = if (order.side == Side.BUY) order.price >= oppositeOrder.price
            else order.price <= oppositeOrder.price

            if (!isMatch) return@forEach  // skip if not a match

            // Determine quantity to trade
            val tradeQty = remainingQty.min(oppositeOrder.quantity)
            remainingQty -= tradeQty
            oppositeOrder.quantity -= tradeQty

            // Record executed trade
            trades.add(
                Trade(
                    price = oppositeOrder.price,
                    quantity = tradeQty,
                    currencyPair = order.currencyPair,
                    takerSide = order.side,
                    tradedAt = Instant.now(),
                    sequenceId = nextSequenceId()
                ).also { logger.info("Trade created: $it") }
            )
        }

        // Remove fully matched opposite orders
        orders[oppositeSide]!!.removeIf { it.quantity <= BigDecimal.ZERO }

        // Add remaining unfilled quantity to the same side order book
        if (remainingQty > BigDecimal.ZERO) {
            val remainingOrder = order.copy(quantity = remainingQty)
            orders[sameSide]!!.add(remainingOrder)

            // Sort bids descending, asks ascending
            val comparator = if (sameSide == Side.BUY) {
                compareByDescending<OrderItem> { it.price }.thenBy { it.orderId }
            } else {
                compareBy<OrderItem> { it.price }.thenBy { it.orderId }
            }
            orders[sameSide]!!.sortWith(comparator)

            logger.info("Added remaining order to book: $remainingOrder")
        }
    }

    /** Converts a LimitOrderRequest to internal OrderItem model */
    private fun mapLimitOrderToOrderItem(limitOrder: LimitOrderRequest) = OrderItem(
        side = limitOrder.side,
        quantity = limitOrder.quantity,
        price = limitOrder.price,
        currencyPair = Pair.valueOf(limitOrder.pair),
        orderCount = 1,
        customerOrderId = limitOrder.customerOrderId
    )

    /** Validates that order fields are correct */
    private fun validateOrder(order: OrderItem) {
        require(order.price > BigDecimal.ZERO) { throw OrderValidationException("Order price must be greater than zero") }
        require(order.quantity > BigDecimal.ZERO) { throw OrderValidationException("Order quantity must be greater than zero") }
        require(order.currencyPair.toString().isNotBlank()) { throw OrderValidationException("Currency pair cannot be blank") }
        logger.debug("Validated order: $order")
    }

    /** Returns the next sequential ID for trade records */
    private fun nextSequenceId(): Long = ++sequenceCounter
}
