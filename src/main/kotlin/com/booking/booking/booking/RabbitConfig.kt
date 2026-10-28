package com.booking.booking.booking

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

object Amqp {
    const val EXCHANGE = "booking.events"
    const val ROUTING_BOOKING_CREATED = "booking.created"
    const val QUEUE_NOTIFICATION = "notifications"
}

@Configuration
class RabbitConfig {
    @Bean
    fun bookingExchange() = TopicExchange(Amqp.EXCHANGE, true, false)

    @Bean
    fun notificationQueue(): Queue = QueueBuilder.durable(Amqp.QUEUE_NOTIFICATION).build()

    @Bean
    fun notificationBinding(): Binding =
        BindingBuilder.bind(notificationQueue()).to(bookingExchange()).with(
            Amqp.ROUTING_BOOKING_CREATED
        )
}