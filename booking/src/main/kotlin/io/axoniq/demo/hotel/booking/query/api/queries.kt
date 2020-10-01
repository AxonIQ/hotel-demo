package io.axoniq.demo.hotel.booking.query.api

import java.util.*

enum class RoomStatus { READY, BOOKED, TAKEN, EMPTY }
enum class PaymentStatus { PROCESSING, SUCCEEDED }

data class FindAccount(val accountId: UUID)
class FindAccounts
data class FindPayment(val paymentId: UUID)
class FindPayments
data class FindPaymentsForAccount(val accountId: UUID)
data class FindRoomAvailabilityForAccount(val roomId: Int, val accountId: UUID)
data class FindRoomAvailability(val roomId: Int)
class FindAllRoomCleaningSchedules()
class FindAllRoomCheckoutSchedules()
