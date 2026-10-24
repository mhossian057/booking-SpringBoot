package com.booking.booking.auth

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val auth: AuthService) {

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody req: RegisterRequest): Map<String, Any?> {
        val user = auth.register(req)
        return mapOf("id" to user.id, "email" to user.email, "role" to user.role)
    }

    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody req: LoginRequest): TokenResponse = auth.login(req)

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal jwt: Jwt) = mapOf(
        "userId" to jwt.subject,
        "email" to jwt.getClaimAsString("email"),
        "roles" to jwt.getClaimAsStringList("roles")
    )

    @GetMapping("/admin/ping")
    fun adminPing() = mapOf("message" to "Hello, admin")
}