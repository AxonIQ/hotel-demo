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
