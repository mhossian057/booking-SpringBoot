package com.booking.booking.booking

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

@Entity
@Table(name = "slots")
class Slot(
    @Column(name = "starts_at", nullable = false)
    val startsAt: Instant,
    @Column(name = "ends_at", nullable = false)
    val endsAt: Instant,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)

@Entity
@Table(name = "bookings")
class Booking(
    @Column(name = "slot_id", nullable = false)
    val slotId: Long,
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val status: String = "CONFIRMED",
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)

interface SlotRepository : JpaRepository<Slot, Long> {
    fun findAllByOrderByStartsAtAsc(): List<Slot>
}

interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Booking>

    @Query("select b.slotId from Booking b")
    fun findAllBookedSlotIds(): List<Long>
}

