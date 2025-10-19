package org.example.orderbook.controller
import orderbook.model.LimitOrderRequest
import orderbook.model.LimitOrderResponse
import orderbook.model.OrderBook
import orderbook.model.Trade
import org.example.orderbook.service.OrderBookService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
open class OrderController(
    private val service: OrderBookService
) {

    @GetMapping("/BTCZAR/orderbook")
    fun getOrderBook(): OrderBook {
        // If service.getOrderBook() throws any exception, it will automatically be handled by GlobalExceptionHandler
        return service.getOrderBook()
    }

    @PostMapping("/orders/limit")
    fun submitLimitOrder(
        @RequestBody request: LimitOrderRequest
    ): LimitOrderResponse {
        // If service.submitLimitOrder() throws any exception, it will automatically be handled by GlobalExceptionHandler
        return service.submitLimitOrder(request)
    }

    @GetMapping("/BTCZAR/tradehistory")
    fun getRecentTrades(): List<Trade> {
        return service.getRecentTrades()
    }

}
