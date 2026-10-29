package com.booking.booking.booking

import java.time.Instant

data class BookingCreatedEvent(
    val eventId: String,
    val bookingId: Long,
    val slotId: Long,
    val userId: Long,
    val occurredAt: Instant,
    val type: String = "BookingCreated",
)