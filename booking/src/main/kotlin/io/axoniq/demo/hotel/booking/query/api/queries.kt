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
