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

package io.axoniq.demo.hotel.booking

import io.axoniq.demo.hotel.booking.command.api.RoomBooking
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

const val USER_NAME = "user@gmail.com"
const val PASS_WORD = "1234"

const val ROOM_NUMBER = 1
const val ROOM_DESCRIPTION = "Double Room with ocean view"

val startDate: LocalDate = LocalDate.parse("2020-07-01")
val endDate: LocalDate = LocalDate.parse("2020-07-31")

val overlappingStartDate: LocalDate = LocalDate.parse("2020-07-15")
val overlappingEndDate: LocalDate = LocalDate.parse("2020-07-17")

val connectedPriorBookingPeriodStartDate: LocalDate = LocalDate.parse("2020-07-01")
val connectedPriorBookingPeriodEndDate: LocalDate = LocalDate.parse("2020-06-15")

val connectedFutureBookingPeriodStartDate: LocalDate = LocalDate.parse("2020-07-31")
val connectedFutureBookingPeriodEndDate: LocalDate = LocalDate.parse("2020-08-15")

val bookingPeriod = RoomBooking(
    startDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    endDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    UUID.fromString("b1c1779d-9169-49c2-8ef4-5233ae8c5471")
)
val bookingPeriod2 = RoomBooking(
    startDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    endDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    UUID.fromString("b1c1779d-9169-49c2-8ef4-5233ae8c5472")
)
val overlappingBookingPeriod = RoomBooking(
    overlappingStartDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    overlappingEndDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    UUID.fromString("b1c1779d-9169-49c2-8ef4-5233ae8c5471")
)
val connectedToPriorBookingPeriod = RoomBooking(
    connectedPriorBookingPeriodStartDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    connectedPriorBookingPeriodEndDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    UUID.fromString("b1c1779d-9169-49c2-8ef4-5233ae8c5471")
)
val connectedToFutureBookingPeriod = RoomBooking(
    connectedFutureBookingPeriodStartDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    connectedFutureBookingPeriodEndDate.atStartOfDay().toInstant(ZoneOffset.UTC),
    UUID.fromString("b1c1779d-9169-49c2-8ef4-5233ae8c5471")
)
