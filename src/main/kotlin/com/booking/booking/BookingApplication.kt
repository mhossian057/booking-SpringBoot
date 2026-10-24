package com.booking.booking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class BookingApplication

fun main(args: Array<String>) {
    runApplication<BookingApplication>(*args)
}
