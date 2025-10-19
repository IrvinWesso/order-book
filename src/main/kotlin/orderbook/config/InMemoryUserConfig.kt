package org.example.orderbook.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager


/*
* SecurityConfig defines what endpoints are protected and how authentication is done (HTTP Basic, CSRF, method-level
* security).

InMemoryUserConfig provides the actual user credentials that Spring Security checks when someone tries to authenticate.

For example:
When a client calls POST /api/orders/limit, Spring Security sees it is protected.
Spring Security asks the UserDetailsService (our InMemoryUserDetailsManager) if the username/password is valid.
If it matches trader1:password123, access is granted.

PasswordEncoder ensures passwords are stored/checked securely. The passwordEncoder bean defined in SecurityConfig is
injected into InMemoryUserConfig so the password can be hashed.*/
// Marks this class as a configuration class for Spring
@Configuration
open class InMemoryUserConfig(val passwordEncoder: PasswordEncoder) {

    @Bean
    open fun userDetailsService(): UserDetailsService {
        val user = User.builder()
            .username("trader1")
            .password(passwordEncoder.encode("password123"))
            .roles("USER")
            .build()

        return InMemoryUserDetailsManager(user)
    }
}