package org.example.orderbook.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.Customizer

/*
* SecurityConfig defines what endpoints are protected and how authentication is done (HTTP Basic, CSRF, method-level
* security).

InMemoryUserConfig provides the actual user credentials that Spring Security checks when someone tries to authenticate.

For example:
When a client calls POST /api/orders/limit, Spring Security sees it is protected.
Spring Security asks the UserDetailsService (our InMemoryUserDetailsManager) if the username/password is valid.
If it matches trader1:password123, access is granted.

PasswordEncoder ensures passwords are stored/checked securely.
* The passwordEncoder bean defined in SecurityConfig is injected into InMemoryUserConfig so the password can be hashed.*/
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
open class SecurityConfig {
    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/orders/limit").authenticated()
                    .requestMatchers("/api/BTCZAR/orderbook", "/api/BTCZAR/tradehistory").permitAll()
            }
            .httpBasic(Customizer.withDefaults())
        return http.build()
    }
}