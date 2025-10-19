package orderbook.config

import org.example.orderbook.config.SecurityConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.SecurityFilterChain
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration

@WebMvcTest // Only load MVC components (useful for testing SecurityConfig)
@ContextConfiguration(classes = [SecurityConfig::class]) // Load the SecurityConfig for the test
class SecurityConfigTest {

    @Autowired
    private lateinit var securityFilterChain: SecurityFilterChain // Autowire the SecurityFilterChain bean

    @Autowired
    private lateinit var securityConfig: SecurityConfig // Autowire the SecurityConfig class

    @Test
    fun `securityFilterChain bean should exist`() {
        // Verify that the SecurityFilterChain bean is properly created
        assertThat(securityFilterChain).isNotNull
    }

    @Test
    fun `passwordEncoder bean should exist`() {
        // Call the passwordEncoder() method and verify it returns a BCryptPasswordEncoder
        val encoder = securityConfig.passwordEncoder()
        assertThat(encoder).isNotNull
        assertThat(encoder).isInstanceOf(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder::class.java)
    }
}
