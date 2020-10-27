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
import io.axoniq.demo.hotel.booking.command.api.BookRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckInCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckOutCommand;
import io.axoniq.demo.hotel.booking.command.api.MarkRoomAsPreparedCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomAddedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomBookedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomBookingRejectedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomCheckedInEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomCheckedOutEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomPreparedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.axoniq.demo.hotel.booking.TestFactoryKt.ROOM_NUMBER;
import static io.axoniq.demo.hotel.booking.TestFactoryKt.getBookingPeriod;
import static io.axoniq.demo.hotel.booking.TestFactoryKt.getBookingPeriod2;

class RoomTest {

    private AggregateTestFixture<Room> testFixture;


    @BeforeEach
    void setUp() {
        testFixture = new AggregateTestFixture<>(Room.class);
    }

    @Test
    void addRoomTest() {
        AddRoomCommand addRoomCommand = new AddRoomCommand(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);

        testFixture.givenNoPriorActivity()
                   .when(addRoomCommand)
                   .expectEvents(roomAddedEvent)
                   .expectSuccessfulHandlerExecution();
    }

    @Test
    void bookRoomWithSuccessTest() {
        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        BookRoomCommand bookRoomCommand = new BookRoomCommand(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());
        RoomBookedEvent roomBookedEvent = new RoomBookedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());

        testFixture.given(roomAddedEvent)
                   .when(bookRoomCommand)
                   .expectEvents(roomBookedEvent)
                   .expectSuccessfulHandlerExecution();
    }


    @Test
    void rejectRoomBookingWithNotAvailableReasonTest2() {
        BookRoomCommand bookRoomCommand = new BookRoomCommand(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());

        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomBookedEvent roomBookedEvent = new RoomBookedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());
        RoomBookingRejectedEvent roomBookingRejectedEvent = new RoomBookingRejectedEvent(TestFactoryKt.ROOM_NUMBER,
                                                                                         getBookingPeriod(),
                                                                                         String.format(Room.ROOM_IS_NOT_AVAILABLE, ROOM_NUMBER));

        testFixture.given(roomAddedEvent, roomBookedEvent)
                   .when(bookRoomCommand)
                   .expectEvents(roomBookingRejectedEvent);
    }


    @Test
    void prepareRoomTest() {
        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomBookedEvent roomBookedEvent = new RoomBookedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());
        MarkRoomAsPreparedCommand markRoomAsPreparedCommand = new MarkRoomAsPreparedCommand(TestFactoryKt.ROOM_NUMBER,
                                                                                            getBookingPeriod()
                                                                                                    .getBookingId());
        RoomPreparedEvent roomPreparedEvent = new RoomPreparedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());

        testFixture.given(roomAddedEvent, roomBookedEvent)
                   .when(markRoomAsPreparedCommand)
                   .expectEvents(roomPreparedEvent)
                   .expectSuccessfulHandlerExecution();
    }

    @Test
    void checkInTest() {
        UUID bookingId = UUID.randomUUID();
        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomBookedEvent roomBookedEvent = new RoomBookedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());
        RoomPreparedEvent roomPreparedEvent = new RoomPreparedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());
        CheckInCommand checkInCommand = new CheckInCommand(TestFactoryKt.ROOM_NUMBER, bookingId);
        RoomCheckedInEvent roomCheckedInEvent = new RoomCheckedInEvent(TestFactoryKt.ROOM_NUMBER, bookingId);

        testFixture.given(roomAddedEvent, roomBookedEvent, roomPreparedEvent)
                   .when(checkInCommand)
                   .expectEvents(roomCheckedInEvent)
                   .expectSuccessfulHandlerExecution();
    }

    @Test
    void checkInFailsRoomNotReadyTest() {
        UUID bookingId = UUID.randomUUID();

        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        CheckInCommand checkInCommand = new CheckInCommand(TestFactoryKt.ROOM_NUMBER, bookingId);

        testFixture.given(roomAddedEvent)
                   .when(checkInCommand)
                   .expectNoEvents()
                   .expectException(IllegalArgumentException.class);
    }

    @Test
    void checkOutTest() {
        UUID bookingId = UUID.randomUUID();
        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        RoomBookedEvent roomBookedEvent = new RoomBookedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());
        RoomPreparedEvent roomPreparedEvent = new RoomPreparedEvent(TestFactoryKt.ROOM_NUMBER, getBookingPeriod());
        RoomCheckedInEvent roomCheckedInEvent = new RoomCheckedInEvent(TestFactoryKt.ROOM_NUMBER, bookingId);
        CheckOutCommand checkOutCommand = new CheckOutCommand(TestFactoryKt.ROOM_NUMBER, bookingId);
        RoomCheckedOutEvent roomCheckedOutEvent = new RoomCheckedOutEvent(TestFactoryKt.ROOM_NUMBER, bookingId);

        testFixture.given(roomAddedEvent, roomBookedEvent, roomPreparedEvent, roomCheckedInEvent)
                   .when(checkOutCommand)
                   .expectEvents(roomCheckedOutEvent)
                   .expectSuccessfulHandlerExecution();
    }

    @Test
    void checkOutOnNonTakenRoomTest() {
        UUID bookingId = UUID.randomUUID();
        RoomAddedEvent roomAddedEvent = new RoomAddedEvent(TestFactoryKt.ROOM_NUMBER, TestFactoryKt.ROOM_DESCRIPTION);
        CheckOutCommand checkOutCommand = new CheckOutCommand(TestFactoryKt.ROOM_NUMBER, bookingId);

        testFixture.given(roomAddedEvent)
                   .when(checkOutCommand)
                   .expectNoEvents()
                   .expectException(IllegalArgumentException.class);
    }
}
