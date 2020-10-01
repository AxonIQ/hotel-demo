package io.axoniq.demo.hotel.booking.command.web.api

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class AccountRequestData(val userName: String, val password: String)
data class RoomRequestData(val roomNumber: Int, val description: String)
data class PayRequestData(val accountId: UUID, val totalAmount: BigDecimal)
data class RoomBookingIdData(val bookingId: UUID)
data class RoomBookingData(val startDate: Instant, val endDate: Instant, val accountID: UUID)
