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
