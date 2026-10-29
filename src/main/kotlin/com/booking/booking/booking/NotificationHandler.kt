package com.booking.booking.booking

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationHandler(private val jdbc: JdbcTemplate) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun handle(messageId: String, event: BookingCreatedEvent): Boolean {
        val inserted = jdbc.update(
            "INSERT INTO processed_messages (message_id) VALUES (?) ON CONFLICT DO NOTHING",
            messageId
        )

        if (inserted == 0) {
            log.info("Duplicate message {} skipped (booking {})", messageId, event.bookingId)
            return false
        }

        val body = "Your booking #${event.bookingId} for slot ${event.slotId} is confirmed."
        jdbc.update(
            "INSERT INTO notifications (message_id, booking_id, user_id, body) VALUES (?, ?, ?, ?)",
            messageId,
            event.bookingId,
            event.userId,
            body
        )
        log.info("NOTIFY user {} -> {}", event.userId, body)
        return true
    }

}