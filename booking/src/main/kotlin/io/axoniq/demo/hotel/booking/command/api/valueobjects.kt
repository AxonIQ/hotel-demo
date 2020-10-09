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

import java.time.Instant
import java.util.*

enum class RoomStatus { PREPARED, CHECKED_IN, EMPTY }
enum class PaymentStatus { PROCESSING, SUCCEEDED, FAILED }
data class RoomBooking(val startDate: Instant, val endDate: Instant, val accountID: UUID, val bookingId: UUID) {
    constructor(startDate: Instant, endDate: Instant, accountID: UUID) : this(startDate, endDate, accountID, UUID.randomUUID())
}
