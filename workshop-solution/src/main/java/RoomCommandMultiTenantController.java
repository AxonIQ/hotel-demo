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
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.extensions.multitenancy.autoconfig.TenantConfiguration;
import org.axonframework.messaging.MetaData;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin
public class RoomCommandMultiTenantController {


    private final CommandGateway commandGateway;

    public RoomCommandMultiTenantController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping(path = "/rooms")
    public CompletableFuture<Integer> addRoom(@RequestBody RoomRequestData roomRequestData,
                                              @RequestHeader(value = "tenant-id", required = false) String tenantId) {
        final AddRoomCommand addRoomCommand = new AddRoomCommand(roomRequestData.getRoomNumber(),
                                                                 roomRequestData.getDescription());
        return commandGateway.send(addRoomCommand, MetaData.with(TenantConfiguration.TENANT_CORRELATION_KEY, tenantId));
    }

    @PostMapping(path = "/rooms/{roomNumber}/book")
    public CompletableFuture<Integer> bookRoom(@PathVariable Integer roomNumber,
                                               @RequestBody RoomBookingData roomBookingData,
                                               @RequestHeader(value = "tenant-id", required = false) String tenantId) {
        final BookRoomCommand bookRoomCommand = new BookRoomCommand(roomNumber,
                                                                    new RoomBooking(roomBookingData.getStartDate(),
                                                                                    roomBookingData.getEndDate(),
                                                                                    roomBookingData.getAccountID()));
        return commandGateway.send(bookRoomCommand, MetaData.with(TenantConfiguration.TENANT_CORRELATION_KEY, tenantId));
    }

    @PostMapping(path = "/rooms/{roomNumber}/prepare")
    public CompletableFuture<Integer> markRoomAsPrepared(@PathVariable Integer roomNumber,
                                                         @RequestBody RoomBookingIdData roomBookingId,
                                                         @RequestHeader(value = "tenant-id", required = false) String tenantId) {
        final MarkRoomAsPreparedCommand markRoomAsPreparedCommand = new MarkRoomAsPreparedCommand(roomNumber, roomBookingId.getBookingId());
        return commandGateway.send(markRoomAsPreparedCommand, MetaData.with(TenantConfiguration.TENANT_CORRELATION_KEY, tenantId));
    }

    @PostMapping(path = "/rooms/{roomNumber}/check-in")
    public CompletableFuture<Integer> checkInRoom(@PathVariable Integer roomNumber,
                                                  @RequestBody RoomBookingIdData roomBookingId,
                                                  @RequestHeader(value = "tenant-id", required = false) String tenantId) {
        final CheckInCommand checkInCommand = new CheckInCommand(roomNumber, roomBookingId.getBookingId());
        return commandGateway.send(checkInCommand, MetaData.with(TenantConfiguration.TENANT_CORRELATION_KEY, tenantId));
    }

    @PostMapping(path = "/rooms/{roomNumber}/check-out")
    public CompletableFuture<Integer> checkOutRoom(@PathVariable Integer roomNumber,
                                                   @RequestBody RoomBookingIdData roomBookingId,
                                                   @RequestHeader(value = "tenant-id", required = false) String tenantId) {
        final CheckOutCommand checkOutCommand = new CheckOutCommand(roomNumber, roomBookingId.getBookingId());
        return commandGateway.send(checkOutCommand, MetaData.with(TenantConfiguration.TENANT_CORRELATION_KEY, tenantId));
    }
}
