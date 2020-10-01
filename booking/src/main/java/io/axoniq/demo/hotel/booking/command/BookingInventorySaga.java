package io.axoniq.demo.hotel.booking.command;

import io.axoniq.demo.hotel.booking.command.api.AddRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomAddedEvent;
import io.axoniq.demo.hotel.inventory.command.api.MarkRoomAsAddedToBookingSystemCommand;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToInventoryEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Saga
@ProcessingGroup("BookingInventorySaga")
public class BookingInventorySaga {

    @Autowired
    private transient CommandGateway commandGateway;
    private UUID inventoryRoomId;

    @StartSaga
    @SagaEventHandler(associationProperty = "roomNumber")
    public void on(RoomAddedToInventoryEvent event) {
        this.inventoryRoomId = event.getRoomId();
        commandGateway.send(new AddRoomCommand(event.getRoomNumber(), event.getRoomDescription()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "roomNumber")
    public void on(RoomAddedEvent event) {
        commandGateway.send(new MarkRoomAsAddedToBookingSystemCommand(this.inventoryRoomId));
    }
}
