package io.axoniq.demo.hotel.inventory.command;

import io.axoniq.demo.hotel.inventory.command.api.AddRoomToInventoryCommand;
import io.axoniq.demo.hotel.inventory.command.api.CreateRoomCommand;
import io.axoniq.demo.hotel.inventory.command.api.MarkRoomAsAddedToBookingSystemCommand;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToBookingSystemEvent;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToInventoryEvent;
import io.axoniq.demo.hotel.inventory.command.api.RoomCreatedEvent;
import io.axoniq.demo.hotel.inventory.command.api.RoomStatus;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
class Room {

    @AggregateIdentifier
    private UUID roomId;
    private Integer roomNumber;
    private RoomStatus roomStatus;
    private String description;

    private Room() {
    }

    @CommandHandler
    Room(CreateRoomCommand command) {
        apply(new RoomCreatedEvent(command.getRoomId(), command.getRoomNumber(), command.getRoomDescription()));
    }

    @CommandHandler
    void handle(AddRoomToInventoryCommand command) {
        Assert.isTrue(RoomStatus.CREATED == this.roomStatus, "This room is not created");
        apply(new RoomAddedToInventoryEvent(command.getRoomId(), this.roomNumber, this.description));
    }

    @CommandHandler
    void handle(MarkRoomAsAddedToBookingSystemCommand command) {
        Assert.isTrue(RoomStatus.IN_INVENTORY == this.roomStatus, "This room is not in inventory");
        apply(new RoomAddedToBookingSystemEvent(command.getRoomId()));
    }

    @EventSourcingHandler
    void on(RoomCreatedEvent event) {
        this.roomId = event.getRoomId();
        this.roomNumber = event.getRoomNumber();
        this.description = event.getRoomDescription();
        this.roomStatus = RoomStatus.CREATED;
    }

    @EventSourcingHandler
    void on(RoomAddedToInventoryEvent event) {
        this.roomStatus = RoomStatus.IN_INVENTORY;
    }

    @EventSourcingHandler
    void on(RoomAddedToBookingSystemEvent event) {
        this.roomStatus = RoomStatus.IN_BOOKING_SYSTEM;
    }
}
