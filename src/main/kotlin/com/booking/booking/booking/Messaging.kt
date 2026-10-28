package com.booking.booking.booking

import java.time.Instant

data class BookingCreatedEvent(
    val eventId: String,
    val bookingId: String,
    val slotId: String,
    val userId: String,
    val occurredAt: Instant,
    val type: String = "BookingCreated",
)