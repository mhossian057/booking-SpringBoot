package com.booking.booking.booking

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageDeliveryMode
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import tools.jackson.databind.json.JsonMapper

@Component
class BookingEventPublisher(
    private val rabbit: RabbitTemplate,
    private val json: JsonMapper,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onBookingCreated(event: BookingCreatedEvent) {
        val props = MessageProperties().apply {
            contentType = MessageProperties.CONTENT_TYPE_JSON
            messageId = event.eventId
            deliveryMode = MessageDeliveryMode.PERSISTENT
            setHeader("eventType", event.type)
        }
        try {
            rabbit.send(
                Amqp.EXCHANGE,
                Amqp.ROUTING_BOOKING_CREATED,
                Message(json.writeValueAsBytes(event), props)
            )
            log.info(
                "Published {} eventId={} bookingId={}",
                event.type,
                event.eventId,
                event.bookingId
            )
        } catch (e: Exception) {
            log.error(
                "FAILED to publish {} eventId={} bookingId={}: {}",
                event.type, event.eventId, event.bookingId, e.message
            )
        }
    }

}