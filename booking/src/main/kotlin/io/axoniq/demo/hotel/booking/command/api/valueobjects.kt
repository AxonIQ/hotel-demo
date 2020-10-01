package io.axoniq.demo.hotel.booking.command.api

import java.time.Instant
import java.util.*

enum class RoomStatus { PREPARED, CHECKED_IN, EMPTY }
enum class PaymentStatus { PROCESSING, SUCCEEDED, FAILED }
data class RoomBooking(val startDate: Instant, val endDate: Instant, val accountID: UUID, val bookingId: UUID) {
    constructor(startDate: Instant, endDate: Instant, accountID: UUID) : this(startDate, endDate, accountID, UUID.randomUUID())
}
