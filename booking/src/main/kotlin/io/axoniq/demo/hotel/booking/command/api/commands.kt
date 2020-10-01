package io.axoniq.demo.hotel.booking.command.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal
import java.util.*

// Account
data class RegisterAccountCommand(
    @TargetAggregateIdentifier val accountId: UUID,
    val userName: String,
    val password: String
)

// Room
data class AddRoomCommand(@TargetAggregateIdentifier val roomNumber: Int, val roomDescription: String)
data class BookRoomCommand(@TargetAggregateIdentifier val roomNumber: Int, val roomBooking: RoomBooking)
data class MarkRoomAsPreparedCommand(@TargetAggregateIdentifier val roomNumber: Int, val roomBookingId: UUID)
data class CheckInCommand(@TargetAggregateIdentifier val roomNumber: Int, val roomBookingId: UUID)
data class CheckOutCommand(@TargetAggregateIdentifier val roomNumber: Int, val roomBookingId: UUID)

// Payment
data class PayCommand(val paymentId: UUID, @TargetAggregateIdentifier val accountId: UUID, val totalAmount: BigDecimal)
data class ProcessPaymentCommand(@TargetAggregateIdentifier val paymentId: UUID)
