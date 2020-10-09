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

import io.axoniq.demo.hotel.booking.TestFactoryKt;
import io.axoniq.demo.hotel.booking.command.api.AddRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomAddedEvent;
import io.axoniq.demo.hotel.inventory.command.api.MarkRoomAsAddedToBookingSystemCommand;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToInventoryEvent;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.axoniq.demo.hotel.booking.TestFactoryKt.ROOM_NUMBER;

class BookingInventorySagaTest {

    private SagaTestFixture<BookingInventorySaga> testFixture;
    private UUID roomId;

    @BeforeEach
    void setUp() {
        testFixture = new SagaTestFixture<>(BookingInventorySaga.class);
        roomId = UUID.randomUUID();
    }

    @Test
    void roomAddedToInventoryEventTest() {
        RoomAddedToInventoryEvent roomAddedToInventoryEvent = new RoomAddedToInventoryEvent(roomId, TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        AddRoomCommand addRoomCommand = new AddRoomCommand(ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);

        testFixture.givenNoPriorActivity()
                   .whenAggregate(roomId.toString())
                   .publishes(roomAddedToInventoryEvent)
                   .expectActiveSagas(1)
                   .expectDispatchedCommands(addRoomCommand);
    }

    @Test
    void roomAddedEventTest() {
        RoomAddedToInventoryEvent roomAddedToInventoryEvent = new RoomAddedToInventoryEvent(roomId, TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        MarkRoomAsAddedToBookingSystemCommand markRoomAsAddedToBookingSystemCommand = new MarkRoomAsAddedToBookingSystemCommand(roomId);

        testFixture.givenAggregate(roomId.toString())
                   .published(roomAddedToInventoryEvent)
                   .whenPublishingA(roomAddedEvent)
                   .expectActiveSagas(0)
                   .expectDispatchedCommands(markRoomAsAddedToBookingSystemCommand);
    }
}
