package com.booking.booking.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.flywaydb.core.internal.parser.TokenType
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

data class RegisterRequest(
    @field:NotBlank @field:Email val email: String,
    @field:Size(min = 8, message = "Password must be at least 8 characters") val password: String
)

data class LoginRequest(
    @field:NotBlank val email: String,
    @field:NotBlank val password: String,
)

data class TokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
)

@Service
class AuthService(
    private val users: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokens: TokenService,
    private val props: JwtProperties,
) {
    fun register(req: RegisterRequest): User {
        val email = req.email.trim().lowercase()
        if (users.existsByEmail(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already registered")
        }
        val hash = passwordEncoder.encode(req.password)!!
        return users.save(User(email, hash, Role.USER))
    }

    fun login(req: LoginRequest): TokenResponse {
        val user = users.findByEmail(req.email.trim().lowercase())
        if (user == null || !passwordEncoder.matches(req.password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
        }
        return TokenResponse(accessToken = tokens.issue(user), expiresIn = props.ttlMinutes * 60)
    }
}