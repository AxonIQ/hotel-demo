/*
 * Copyright (c) 2022-2022. AxonIQ
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

package io.axoniq.demo.hotel.inventory.query

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class RoomEntity(@Id var roomId: String, var roomNumber: Int, var description: String, var addedToInventory: Boolean = false, var addedToBooking: Boolean)
