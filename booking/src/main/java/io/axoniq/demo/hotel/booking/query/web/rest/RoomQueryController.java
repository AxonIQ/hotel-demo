package io.axoniq.demo.hotel.booking.query.web.rest;

import io.axoniq.demo.hotel.booking.query.api.FindAllRoomCheckoutSchedules;
import io.axoniq.demo.hotel.booking.query.api.FindAllRoomCleaningSchedules;
import io.axoniq.demo.hotel.booking.query.api.FindRoomAvailability;
import io.axoniq.demo.hotel.booking.query.api.FindRoomAvailabilityForAccount;
import io.axoniq.demo.hotel.booking.query.api.RoomAvailabilityResponseData;
import io.axoniq.demo.hotel.booking.query.api.RoomCheckoutScheduleData;
import io.axoniq.demo.hotel.booking.query.api.RoomCleaningScheduleData;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class RoomQueryController {

    private final ReactorQueryGateway reactorQueryGateway;

    public RoomQueryController(ReactorQueryGateway reactorQueryGateway) {
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping(path = "/rooms/{roomId}/availability")
    public Mono<RoomAvailabilityResponseData> roomAvailability(@PathVariable Integer roomId) {
        return reactorQueryGateway.query(new FindRoomAvailability(roomId), ResponseTypes.instanceOf(RoomAvailabilityResponseData.class));
    }

    @GetMapping(path = "/rooms/{roomId}/account/{accountId}/availability")
    public Mono<RoomAvailabilityResponseData> roomAvailabilityForAccount(@PathVariable Integer roomId, @PathVariable UUID accountId) {
        return reactorQueryGateway.query(new FindRoomAvailabilityForAccount(roomId, accountId), ResponseTypes.instanceOf(RoomAvailabilityResponseData.class));
    }

    @GetMapping(path = "/rooms/cleaningschedule")
    public Mono<List<RoomCleaningScheduleData>> roomsCleaningSchedule() {
        return reactorQueryGateway.query(new FindAllRoomCleaningSchedules(), ResponseTypes.multipleInstancesOf(RoomCleaningScheduleData.class));
    }

    @GetMapping(path = "/rooms/checkoutschedule")
    public Mono<List<RoomCheckoutScheduleData>> roomsCheckoutSchedule() {
        return reactorQueryGateway.query(new FindAllRoomCheckoutSchedules(), ResponseTypes.multipleInstancesOf(RoomCheckoutScheduleData.class));
    }
}
