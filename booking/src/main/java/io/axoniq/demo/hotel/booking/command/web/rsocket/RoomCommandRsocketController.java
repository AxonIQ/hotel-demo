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

package io.axoniq.demo.hotel.booking.command.web.rsocket;

import io.axoniq.demo.hotel.booking.command.api.AddRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.BookRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckInCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckOutCommand;
import io.axoniq.demo.hotel.booking.command.api.MarkRoomAsPreparedCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomBooking;
import io.axoniq.demo.hotel.booking.command.web.api.RoomBookingData;
import io.axoniq.demo.hotel.booking.command.web.api.RoomBookingIdData;
import io.axoniq.demo.hotel.booking.command.web.api.RoomRequestData;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class RoomCommandRsocketController {

    private final ReactorCommandGateway reactorCommandGateway;

    public RoomCommandRsocketController(ReactorCommandGateway reactorCommandGateway) {
        this.reactorCommandGateway = reactorCommandGateway;
    }

    @MessageMapping("rooms.add")
    public Mono<Integer> addRoom(@Payload RoomRequestData roomRequestData) {
        return reactorCommandGateway.send(new AddRoomCommand(roomRequestData.getRoomNumber(), roomRequestData.getDescription()));
    }

    @MessageMapping("rooms.{roomNumber}.book")
    public Mono<Void> bookRoom(@DestinationVariable Integer roomNumber, @Payload RoomBookingData roomBookingData) {
        return reactorCommandGateway.send(new BookRoomCommand(roomNumber, new RoomBooking(roomBookingData.getStartDate(), roomBookingData.getEndDate(), roomBookingData.getAccountID())));
    }

    @MessageMapping("rooms.{roomNumber}.markprepared")
    public Mono<Void> markRoomAsPrepared(@DestinationVariable Integer roomNumber, @Payload RoomBookingIdData roomBookingId) {
        return reactorCommandGateway.send(new MarkRoomAsPreparedCommand(roomNumber, roomBookingId.getBookingId()));
    }

    @MessageMapping("rooms.{roomNumber}.checkin")
    public Mono<Void> checkInRoom(@DestinationVariable Integer roomNumber, @Payload RoomBookingIdData roomBookingId) {
        return reactorCommandGateway.send(new CheckInCommand(roomNumber, roomBookingId.getBookingId()));
    }

    @MessageMapping("rooms.{roomNumber}.checkout")
    public Mono<Void> checkOutRoom(@DestinationVariable Integer roomNumber, @Payload RoomBookingIdData roomBookingId) {
        return reactorCommandGateway.send(new CheckOutCommand(roomNumber, roomBookingId.getBookingId()));
    }
}
