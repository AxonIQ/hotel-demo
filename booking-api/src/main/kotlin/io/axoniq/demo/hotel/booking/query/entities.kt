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
