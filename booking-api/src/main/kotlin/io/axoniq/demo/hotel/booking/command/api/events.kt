/*
 * Copyright (c) 2020-2020. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;)
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
