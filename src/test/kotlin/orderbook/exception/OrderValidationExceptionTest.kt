package orderbook.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OrderValidationExceptionTest {

    @Test
    fun `should store the exception message`() {
        val message = "Order price must be greater than zero"
        val ex = OrderValidationException(message)

        // Check that the message is stored correctly
        assertEquals(message, ex.message)

        // Check that it is a RuntimeException
        assertTrue(ex is RuntimeException)
    }

    @Test
    fun `should have null cause by default`() {
        val ex = OrderValidationException("Test message")

        // Since we did not provide a cause, it should be null
        assertNull(ex.cause)
    }
}