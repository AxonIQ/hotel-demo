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
