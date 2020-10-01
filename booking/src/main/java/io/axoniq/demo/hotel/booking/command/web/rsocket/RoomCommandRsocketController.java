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
