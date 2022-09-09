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
