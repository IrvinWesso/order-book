package orderbook.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

data class ErrorResponse(
    val timestamp: Instant = Instant.now(),
    val message: String,
    val status: Int
)

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(OrderValidationException::class)
    fun handleValidation(e: OrderValidationException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(message = e.message ?: "Invalid order", status = HttpStatus.BAD_REQUEST.value())
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(OrderNotFoundException::class)
    fun handleNotFound(e: OrderNotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(message = e.message ?: "Order not found", status = HttpStatus.NOT_FOUND.value())
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(e: Exception): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(message = e.message ?: "Unexpected error", status = HttpStatus.INTERNAL_SERVER_ERROR.value())
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
