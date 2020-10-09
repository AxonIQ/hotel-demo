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

package io.axoniq.demo.hotel.booking.command;

import org.axonframework.modelling.command.EntityId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

class Booking {

    @EntityId
    private UUID roomBookingId;
    private Instant startDate;
    private Instant endDate;
    private UUID accountID;

    Booking(UUID roomBookingId, Instant startDate, Instant endDate, UUID accountID) {
        this.roomBookingId = roomBookingId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.accountID = accountID;
    }

    UUID getRoomBookingId() {
        return roomBookingId;
    }

    Instant getStartDate() {
        return startDate;
    }

    Instant getEndDate() {
        return endDate;
    }

    UUID getAccountID() {
        return accountID;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Booking that = (Booking) o;
        return roomBookingId.equals(that.roomBookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomBookingId);
    }
}
