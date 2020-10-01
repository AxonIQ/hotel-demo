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
