package com.booking.booking.auth

import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(private val encoder: JwtEncoder, private val props: JwtProperties) {

    fun issue(user: User): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer("booking-service")
            .issuedAt(now)
            .expiresAt(now.plus(props.ttlMinutes, ChronoUnit.MINUTES))
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("roles", listOf(user.role.name))
            .build()

        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return encoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }

}