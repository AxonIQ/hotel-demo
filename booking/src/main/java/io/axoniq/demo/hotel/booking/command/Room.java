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

import io.axoniq.demo.hotel.booking.command.api.AddRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.BookRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckInCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckOutCommand;
import io.axoniq.demo.hotel.booking.command.api.MarkRoomAsPreparedCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomAddedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomBookedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomBooking;
import io.axoniq.demo.hotel.booking.command.api.RoomBookingRejectedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomCheckedInEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomCheckedOutEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomPreparedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomStatus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(snapshotTriggerDefinition = "roomSnapshotTriggerDefinition", cache = "cache")
class Room {

    public static final String ROOM_IS_NOT_AVAILABLE = "Room %s is not available";

    @AggregateIdentifier
    private Integer roomNumber;
    private RoomStatus roomStatus;
    @AggregateMember
    private final List<Booking> bookings = new ArrayList<>();

    private Room() {
    }

    @CommandHandler
    Room(AddRoomCommand command) {
        apply(new RoomAddedEvent(command.getRoomNumber(), command.getRoomDescription()));
    }

    //TODO Check Account invariant !!!!
    @CommandHandler
    void handle(BookRoomCommand command) {
        if (isBookingAllowed(command.getRoomBooking())) {
            apply(new RoomBookedEvent(command.getRoomNumber(), command.getRoomBooking()));
        } else {
            apply(new RoomBookingRejectedEvent(command.getRoomNumber(),
                                               command.getRoomBooking(),
                                               String.format(ROOM_IS_NOT_AVAILABLE, roomNumber)));
        }
    }


    @CommandHandler
    void handle(MarkRoomAsPreparedCommand command) {
        this.bookings.stream()
                     .filter(booking -> booking.getRoomBookingId().equals(command.getRoomBookingId()))
                     .findAny().ifPresent(booking -> apply(new RoomPreparedEvent(command.getRoomNumber(),
                                                                                 new RoomBooking(
                                                                                         booking.getStartDate(),
                                                                                         booking.getEndDate(),
                                                                                         booking.getAccountID(),
                                                                                         booking
                                                                                                 .getRoomBookingId()))));
    }

    @CommandHandler
    void handle(CheckInCommand command) {
        Assert.isTrue(roomIsPrepared(), String.format("Room %s is not prepared", roomNumber));
        apply(new RoomCheckedInEvent(command.getRoomNumber(), command.getRoomBookingId()));
    }

    @CommandHandler
    void handle(CheckOutCommand command) {
        Assert.isTrue(roomIsCheckedIn(), String.format("Room %s is not checked-in", roomNumber));
        apply(new RoomCheckedOutEvent(command.getRoomNumber(), command.getRoomBookingId()));
    }

    @EventSourcingHandler
    void on(RoomAddedEvent event) {
        this.roomNumber = event.getRoomNumber();
        this.roomStatus = RoomStatus.EMPTY;
    }

    @EventSourcingHandler
    void on(RoomBookedEvent event) {
        this.bookings.add(new Booking(event.getRoomBooking().getBookingId(),
                                      event.getRoomBooking().getStartDate(),
                                      event.getRoomBooking().getEndDate(),
                                      event.getRoomBooking().getAccountID()));
    }

    @EventSourcingHandler
    void on(RoomPreparedEvent event) {
        this.roomStatus = RoomStatus.PREPARED;
    }

    @EventSourcingHandler
    void on(RoomCheckedInEvent event) {
        this.roomStatus = RoomStatus.CHECKED_IN;
    }

    @EventSourcingHandler
    void on(RoomCheckedOutEvent event) {
        this.roomStatus = RoomStatus.EMPTY;
    }

    private boolean roomIsPrepared() {
        return RoomStatus.PREPARED.equals(roomStatus);
    }

    private boolean roomIsCheckedIn() {
        return RoomStatus.CHECKED_IN.equals(roomStatus);
    }

    private boolean isBookingAllowed(RoomBooking roomBooking) {
        return bookings.stream().noneMatch(booking -> booking.getStartDate().isBefore(roomBooking.getEndDate())
                && booking.getEndDate().isAfter(roomBooking.getStartDate()));
    }
}
