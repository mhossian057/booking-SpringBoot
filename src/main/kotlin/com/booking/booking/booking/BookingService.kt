package com.booking.booking.booking

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

data class CreatedSlotRequest(val startsAt: Instant, val endsAt: Instant)
data class BookingRequest(val slotId: Long)

data class SlotView(val id: Long, val startsAt: Instant, val endsAt: Instant, val booked: Boolean)
data class BookingView(
    val id: Long, val slotId: Long, val userId: Long, val status: String, val createdAt: Instant
)

fun Booking.toView() = BookingView(id!!, slotId, userId, status, createdAt)

@Service
class BookingService(private val slots: SlotRepository, private val bookings: BookingRepository) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun createSlot(req: CreatedSlotRequest): SlotView {
        if (!req.endsAt.isAfter(req.startsAt)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "endsAt must be after startsAt")
        }
        try {
            val slot = slots.saveAndFlush(Slot(startsAt = req.startsAt, endsAt = req.endsAt))
            return SlotView(slot.id!!, slot.startsAt, slot.endsAt, booked = false)
        } catch (e: DataIntegrityViolationException) {
            if (e.violates("uq_slots_start")) {
                throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    $$"A slot already starts at ${req.startsAt}"
                )
            }
            throw e
        }
    }

    fun listSlots(): List<SlotView> {
        val booked = bookings.findAllBookedSlotIds().toSet()
        return slots.findAllByOrderByStartsAtAsc().map {
            SlotView(it.id!!, it.startsAt, it.endsAt, booked = it.id in booked)
        }
    }

    @Transactional
    fun book(slotId: Long, userId: Long): BookingView {
        if (!slots.existsById(slotId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Slot $slotId not found")
        }
        try {
            val booking = bookings.saveAndFlush(Booking(slotId = slotId, userId = userId))
            log.info("Booking {} created: slot={} user={}", booking.id, slotId, userId)
            return booking.toView()
        } catch (e: DataIntegrityViolationException) {
            if (e.violates("uq_booking_slots")) {
                log.info("Rejected double booking: slot={} user={}", slotId, userId)
                throw ResponseStatusException(HttpStatus.CONFLICT, "Slot $slotId is already booked")
            }
            throw e
        }
    }

    fun myBooking(userId: Long): List<BookingView> =
        bookings.findByUserIdOrderByCreatedAtDesc(userId).map {
            it.toView()
        }

    private fun DataIntegrityViolationException.violates(constraint: String): Boolean =
        mostSpecificCause.message?.contains(constraint) == true
}