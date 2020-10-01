package io.axoniq.demo.hotel.booking.command.api

import java.math.BigDecimal
import java.util.*

// Account
data class AccountRegisteredEvent(val accountId: UUID, val userName: String, val password: String)
// Room
data class RoomAddedEvent(val roomNumber: Int, val roomDescription: String)
data class RoomBookedEvent(val roomNumber: Int, val roomBooking: RoomBooking)
data class RoomBookingRejectedEvent(val roomNumber: Int, val roomBooking: RoomBooking, val reason: String)
data class RoomPreparedEvent(val roomNumber: Int, val roomBooking: RoomBooking)
data class RoomCheckedInEvent(val roomNumber: Int, val roomBookingId: UUID)
data class RoomCheckedOutEvent(val roomNumber: Int, val roomBookingId: UUID)
// Payment
data class PaymentRequestedEvent(val paymentId: UUID, val accountId: UUID, val totalAmount: BigDecimal)
data class PaymentSucceededEvent(val paymentId: UUID)
