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

package io.axoniq.demo.hotel.inventory.command;

import io.axoniq.demo.hotel.inventory.TestFactoryKt;
import io.axoniq.demo.hotel.inventory.command.api.AddRoomToInventoryCommand;
import io.axoniq.demo.hotel.inventory.command.api.CreateRoomCommand;
import io.axoniq.demo.hotel.inventory.command.api.MarkRoomAsAddedToBookingSystemCommand;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToBookingSystemEvent;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToInventoryEvent;
import io.axoniq.demo.hotel.inventory.command.api.RoomCreatedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.*;

public class RoomTest {

    private AggregateTestFixture<Room> testFixture;


    @BeforeEach
    void setUp() {
        testFixture = new AggregateTestFixture<>(Room.class);
    }

    @Test
    void createRoomTest() {
        CreateRoomCommand createRoomCommand = new CreateRoomCommand(TestFactoryKt.getROOM_ID(), TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomCreatedEvent roomCreatedEvent = new RoomCreatedEvent(TestFactoryKt.getROOM_ID(), TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);

        testFixture.givenNoPriorActivity()
                   .when(createRoomCommand)
                   .expectEvents(roomCreatedEvent)
                   .expectSuccessfulHandlerExecution();
    }

    @Test
    void addRoomToInventoryTest() {
        RoomCreatedEvent roomCreatedEvent = new RoomCreatedEvent(TestFactoryKt.getROOM_ID(), TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomAddedToInventoryEvent roomAddedToInventoryEvent = new RoomAddedToInventoryEvent(TestFactoryKt.getROOM_ID(), TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        AddRoomToInventoryCommand addRoomToInventoryCommand = new AddRoomToInventoryCommand(TestFactoryKt.getROOM_ID());

        testFixture.given(roomCreatedEvent)
                   .when(addRoomToInventoryCommand)
                   .expectEvents(roomAddedToInventoryEvent);
    }

    @Test
    void addRoomToBookingSystemTest() {
        RoomCreatedEvent roomCreatedEvent = new RoomCreatedEvent(TestFactoryKt.getROOM_ID(), TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomAddedToInventoryEvent roomAddedToInventoryEvent = new RoomAddedToInventoryEvent(TestFactoryKt.getROOM_ID(), TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomAddedToBookingSystemEvent roomAddedToBookingSystemEvent = new RoomAddedToBookingSystemEvent(TestFactoryKt.getROOM_ID());

        MarkRoomAsAddedToBookingSystemCommand markRoomAsAddedToBookingSystemCommand = new MarkRoomAsAddedToBookingSystemCommand(TestFactoryKt.getROOM_ID());

        testFixture.given(roomCreatedEvent, roomAddedToInventoryEvent)
                   .when(markRoomAsAddedToBookingSystemCommand)
                   .expectEvents(roomAddedToBookingSystemEvent);
    }
}
