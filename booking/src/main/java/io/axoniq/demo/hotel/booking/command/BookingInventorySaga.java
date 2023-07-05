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
import io.axoniq.demo.hotel.booking.command.api.RoomAddedEvent;
import io.axoniq.demo.hotel.inventory.command.api.MarkRoomAsAddedToBookingSystemCommand;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToInventoryEvent;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
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
        commandGateway.send(new MarkRoomAsAddedToBookingSystemCommand(this.inventoryRoomId)).join();
    }
}
