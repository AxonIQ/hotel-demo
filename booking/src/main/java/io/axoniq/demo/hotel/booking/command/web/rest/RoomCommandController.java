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

package io.axoniq.demo.hotel.booking.command.web.rest;

import io.axoniq.demo.hotel.booking.command.api.AddRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.BookRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckInCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckOutCommand;
import io.axoniq.demo.hotel.booking.command.api.MarkRoomAsPreparedCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomBooking;
import io.axoniq.demo.hotel.booking.command.web.api.RoomBookingData;
import io.axoniq.demo.hotel.booking.command.web.api.RoomBookingIdData;
import io.axoniq.demo.hotel.booking.command.web.api.RoomRequestData;
import io.axoniq.demo.hotel.booking.query.api.FindRoomAvailability;
import io.axoniq.demo.hotel.booking.query.api.FindRoomAvailabilityForAccount;
import io.axoniq.demo.hotel.booking.query.api.RoomAvailabilityResponseData;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@RestController
@CrossOrigin
public class RoomCommandController {

    public static final int TIMEOUT_SECONDS = 5;

    private final ReactorCommandGateway reactorCommandGateway;
    private final ReactorQueryGateway reactorQueryGateway;

    public RoomCommandController(ReactorCommandGateway reactorCommandGateway, ReactorQueryGateway reactorQueryGateway) {
        this.reactorCommandGateway = reactorCommandGateway;
        this.reactorQueryGateway = reactorQueryGateway;
    }

    private Mono<RoomAvailabilityResponseData> subscribeToRoomUpdates(Integer roomId) {
        Flux<RoomAvailabilityResponseData> queryResult = reactorQueryGateway
                .queryUpdates(new FindRoomAvailability(roomId),
                              ResponseTypes.instanceOf(RoomAvailabilityResponseData.class));
        return queryResult.next().timeout(Duration.ofSeconds(TIMEOUT_SECONDS));
    }

    private Mono<RoomAvailabilityResponseData> subscribeToRoomForAccount(Integer roomId, UUID accountId) {
        Flux<RoomAvailabilityResponseData> queryResult = reactorQueryGateway
                .queryUpdates(new FindRoomAvailabilityForAccount(roomId, accountId),
                              ResponseTypes.instanceOf(RoomAvailabilityResponseData.class));
        return queryResult.next().timeout(Duration.ofSeconds(TIMEOUT_SECONDS));
    }


    @PostMapping(path = "/rooms")
    public Mono<Integer> addRoom(@RequestBody RoomRequestData roomRequestData) {
        return Mono.when(subscribeToRoomUpdates(roomRequestData.getRoomNumber()))
                   .and(reactorCommandGateway.send(new AddRoomCommand(roomRequestData.getRoomNumber(),
                                                                      roomRequestData.getDescription())))
                   .then(Mono.just(roomRequestData.getRoomNumber()));
    }

    @PostMapping(path = "/rooms/{roomNumber}/booked")
    public Mono<Integer> bookRoom(@PathVariable Integer roomNumber, @RequestBody RoomBookingData roomBookingData) {
        return Mono.when(subscribeToRoomForAccount(roomNumber, roomBookingData.getAccountID()))
                   .and(reactorCommandGateway.send(new BookRoomCommand(roomNumber, new RoomBooking(roomBookingData.getStartDate(), roomBookingData.getEndDate(), roomBookingData.getAccountID()))))
                   .then(Mono.just(roomNumber));
    }

    @PostMapping(path = "/rooms/{roomNumber}/prepared")
    public Mono<Integer> markRoomAsPrepared(@PathVariable Integer roomNumber,
                                            @RequestBody RoomBookingIdData roomBookingId) {
        return reactorCommandGateway.send(new MarkRoomAsPreparedCommand(roomNumber, roomBookingId.getBookingId()));
    }

    @PostMapping(path = "/rooms/{roomNumber}/checkedin")
    public Mono<Integer> checkInRoom(@PathVariable Integer roomNumber, @RequestBody RoomBookingIdData roomBookingId) {
        return reactorCommandGateway.send(new CheckInCommand(roomNumber, roomBookingId.getBookingId()));
    }

    @PostMapping(path = "/rooms/{roomNumber}/checkedout")
    public Mono<Integer> checkOutRoom(@PathVariable Integer roomNumber, @RequestBody RoomBookingIdData roomBookingId) {
        return Mono.when(subscribeToRoomUpdates(roomNumber))
                   .and(reactorCommandGateway.send(new CheckOutCommand(roomNumber, roomBookingId.getBookingId())))
                   .then(Mono.just(roomNumber));
    }
}
