package com.booking.booking.auth

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminSeeder(
    private val users: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.admin.email}") private val email: String,
    @Value("\${app.admin.password}") private val password: String,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        if (users.existsByEmail(email)) return

        users.save(User(email, passwordEncoder.encode(password)!!, Role.ADMIN))
        log.info("Created admin user {}", email)
    }
}