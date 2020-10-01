package io.axoniq.demo.hotel.booking.query

import io.axoniq.demo.hotel.booking.query.api.PaymentStatus
import io.axoniq.demo.hotel.booking.query.api.RoomStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
data class AccountEntity(@Id var accountId: String, var userName: String, var password: String)

@Embeddable
data class BookingEmbeddable(var id: String, var startDate: Instant, var endDate: Instant, var accountId: String)

@Embeddable
data class FailedBookingEmbeddable(
    var id: String,
    var startDate: Instant?,
    var endDate: Instant?,
    var accountId: String,
    var reason: String
)

@Entity
data class RoomAvailabilityEntity(
    @Id var roomNumber: Int,
    var roomDescription: String,
    @Enumerated(EnumType.STRING) var roomStatus: RoomStatus,
    @ElementCollection var bookings: List<BookingEmbeddable>,
    @ElementCollection var failedBookings: List<FailedBookingEmbeddable>
)

@Entity
data class RoomCleaningScheduleEntity(
    @Id var roomNumber: Int,
    @ElementCollection @OrderBy("startDate DESC") var bookings: List<BookingEmbeddable>
)

@Entity
data class RoomCheckoutScheduleEntity(
    @Id var roomNumber: Int,
    @Enumerated(EnumType.STRING) var roomStatus: RoomStatus,
    @ElementCollection @OrderBy("startDate DESC") var bookings: List<BookingEmbeddable>
)

@Entity
data class PaymentEntity(@Id var paymentId: String, var accountId: UUID, var totalAmount: BigDecimal, @Enumerated(EnumType.STRING) var paymentStatus: PaymentStatus)
