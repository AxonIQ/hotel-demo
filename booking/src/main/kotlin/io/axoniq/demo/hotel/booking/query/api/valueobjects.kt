package io.axoniq.demo.hotel.booking.query.api

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class AccountResponseData(val accountId: String, val userName: String, val password: String)
data class BookingData(val id: String, val startDate: Instant, val endDate: Instant)
data class FailedBookingData(val startDate: Instant?, val endDate: Instant?, val reason: String)
data class RoomAvailabilityResponseData(val roomNumber: Int, val roomDescription: String, val roomStatus: RoomStatus, val bookings: List<BookingData>, val myFailedBookings: List<FailedBookingData>)
data class RoomCleaningScheduleData(val roomNumber: Int, val deadlines: List<Instant>)
data class RoomCheckoutScheduleData(val roomNumber: Int, val bookings: List<BookingData>)
data class PaymentResponseData(val paymentId: String, val accountId: UUID, val totalAmount: BigDecimal, val paymentStatus: PaymentStatus)
