package com.booking.booking.booking

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class NotificationListener(
    private val handler: NotificationHandler,
    private val json: JsonMapper,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [Amqp.QUEUE_NOTIFICATION])
    fun onMessage(message: Message) {
        val messageId = message.messageProperties.messageId

        val event = json.readValue(message.body, BookingCreatedEvent::class.java)
        log.info("Received {} messageId={} bookingId={}", event.type, messageId, event.bookingId)
        handler.handle(messageId!!, event)
    }

}