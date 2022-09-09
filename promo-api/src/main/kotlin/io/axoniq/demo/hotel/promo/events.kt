package io.axoniq.demo.hotel.promo

import java.time.*
import java.util.*

data class BookingDate(val day: Int, val month: Int, val year: Int) {
    constructor(instant: Instant, zoneId: ZoneId) : this(
        instant.atZone(zoneId).dayOfMonth,
        instant.atZone(zoneId).monthValue,
        instant.atZone(zoneId).year,
    )

    fun asCheckIn(zoneId: ZoneId): Instant =
        LocalDateTime.of(year, Month.of(month), day, 15, 0).atZone(zoneId).toInstant()

    fun asCheckOut(zoneId: ZoneId): Instant =
        LocalDateTime.of(year, Month.of(month), day, 12, 0).atZone(zoneId).toInstant()
}

data class RoomNumberRange(val from: Int, val toIncluding: Int)
enum class FailureReason {
    NO_ROOMS_AVAILABLE
}

data class PromoBookingAttempt(
    val roomNumberRange: RoomNumberRange,
    val startDate: BookingDate,
    val endDate: BookingDate,
    val promoBookingId: UUID
)

data class PromoBookingSucceeded(
    val roomNumber: Int,
    val start: Instant,
    val end: Instant,
    val bookingId: UUID,
    val promoBookingId: UUID
)

data class PromoBookingFailed(
    val failureReason: FailureReason,
    val promoBookingId: UUID
)
