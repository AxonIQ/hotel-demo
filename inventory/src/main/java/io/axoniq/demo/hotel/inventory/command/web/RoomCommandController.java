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

package io.axoniq.demo.hotel.inventory.command.web;

import io.axoniq.demo.hotel.inventory.command.api.AddRoomToInventoryCommand;
import io.axoniq.demo.hotel.inventory.command.api.CreateRoomCommand;
import io.axoniq.demo.hotel.inventory.command.web.api.RoomRequestData;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@CrossOrigin
public class RoomCommandController {

    private final ReactorCommandGateway reactorCommandGateway;

    public RoomCommandController(ReactorCommandGateway reactorCommandGateway) {
        this.reactorCommandGateway = reactorCommandGateway;
    }

    @PostMapping(path = "/rooms")
    public Mono<UUID> createRoom(@RequestBody RoomRequestData roomRequestData) {
        return reactorCommandGateway.send(new CreateRoomCommand(UUID.randomUUID(),
                                                                roomRequestData.getRoomNumber(),
                                                                roomRequestData.getDescription()));
    }

    @PostMapping(path = "/rooms/{id}/ininventory")
    public Mono<UUID> addRoomToInventory(@PathVariable UUID id) {
        return reactorCommandGateway.send(new AddRoomToInventoryCommand(id));
    }
}
