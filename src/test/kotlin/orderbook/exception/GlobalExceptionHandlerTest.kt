package orderbook.exception

import org.example.orderbook.exception.GlobalExceptionHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.Instant

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `handleValidation should return BAD_REQUEST`() {
        val ex = OrderValidationException("Price must be greater than zero")
        val response = handler.handleValidation(ex)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Price must be greater than zero", response.body?.message)
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.body?.status)
        assertTrue(response.body?.timestamp!! <= Instant.now())
    }

    @Test
    fun `handleNotFound should return NOT_FOUND`() {
        val ex = OrderNotFoundException("Order 123 not found")
        val response = handler.handleNotFound(ex)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Order 123 not found", response.body?.message)
        assertEquals(HttpStatus.NOT_FOUND.value(), response.body?.status)
        assertTrue(response.body?.timestamp!! <= Instant.now())
    }

    @Test
    fun `handleGeneral should return INTERNAL_SERVER_ERROR`() {
        val ex = RuntimeException("Something went wrong")
        val response = handler.handleGeneral(ex)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body)
        assertEquals("Something went wrong", response.body?.message)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.body?.status)
        assertTrue(response.body?.timestamp!! <= Instant.now())
    }
}