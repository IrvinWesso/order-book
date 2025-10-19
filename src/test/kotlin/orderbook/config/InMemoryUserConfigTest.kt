package orderbook.config

import org.example.orderbook.config.InMemoryUserConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager

class InMemoryUserConfigTest {

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    private val config = InMemoryUserConfig(passwordEncoder)
    private val userDetailsService = config.userDetailsService() as InMemoryUserDetailsManager

    @Test
    fun `userDetailsService should contain trader1`() {
        val userDetails: UserDetails = userDetailsService.loadUserByUsername("trader1")

        // Verify username
        assertEquals("trader1", userDetails.username)

        // Verify password matches (BCrypt)
        assertTrue(passwordEncoder.matches("password123", userDetails.password))

        // Verify role is USER
        assertTrue(userDetails.authorities.any { it.authority == "ROLE_USER" })
    }

    @Test
    fun `loading non-existing user should throw exception`() {
        assertThrows(Exception::class.java) {
            userDetailsService.loadUserByUsername("nonexistent")
        }
    }
}