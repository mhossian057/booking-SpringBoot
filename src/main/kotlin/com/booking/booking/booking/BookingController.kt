package com.booking.booking.booking

import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

private fun Jwt.userId(): Long = requireNotNull(subject).toLong()

@RestController
class BookingController(private val service: BookingService) {
    @PostMapping("/admin/slots")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSlot(@RequestBody req: CreatedSlotRequest) = service.createSlot(req)

    @GetMapping("/slots")
    fun listSlots() = service.listSlots()

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    fun book(@RequestBody req: BookingRequest, @AuthenticationPrincipal jwt: Jwt) = service.book(req.slotId,jwt.userId())

    @GetMapping("/bookings/me")
    fun myBooking(@AuthenticationPrincipal jwt: Jwt) = service.myBooking(jwt.userId())
}