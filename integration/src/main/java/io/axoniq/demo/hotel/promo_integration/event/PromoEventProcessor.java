package io.axoniq.demo.hotel.promo_integration.event;

import io.axoniq.demo.hotel.booking.command.api.BookRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomBooking;
import io.axoniq.demo.hotel.booking.query.api.FindRoomAvailability;
import io.axoniq.demo.hotel.booking.query.api.RoomAvailabilityResponseData;
import io.axoniq.demo.hotel.promo.PromoBookingAttempt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.MetaData;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.IntStream;

import static io.axoniq.demo.hotel.promo_integration.IntegrationApplication.PROMO_PROCESSOR_NAME;

@Slf4j
@Component
@ProcessingGroup(PROMO_PROCESSOR_NAME)
public class PromoEventProcessor {

    private static final ZoneId AMSTERDAM_ZONE_ID = ZoneId.of("Europe/Amsterdam");
    private static final UUID PROMO_ACCOUNT_ID = UUID.fromString("cafebabe-cafe-babe-cafe-babecafebabe");

    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;

    public PromoEventProcessor(QueryGateway queryGateway, CommandGateway commandGateway) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(PromoBookingAttempt event, Message<?> eventMessage) {
        log.info("Received event: '{}' with id: '{}' and metadata: '{}'",
                 event, eventMessage.getIdentifier(), eventMessage.getMetaData());
        Instant startDate = event.getStartDate().asCheckIn(AMSTERDAM_ZONE_ID);
        Instant endDate = event.getEndDate().asCheckOut(AMSTERDAM_ZONE_ID);
        int roomNumber = IntStream.range(event.getRoomNumberRange().getFrom(),
                                         event.getRoomNumberRange().getToIncluding())
                                  .filter(r -> isVacant(r, startDate, endDate))
                                  .findFirst()
                                  .orElse(event.getRoomNumberRange().getToIncluding());
        RoomBooking roomBooking = new RoomBooking(startDate, endDate, PROMO_ACCOUNT_ID);
        BookRoomCommand command = new BookRoomCommand(roomNumber, roomBooking);
        commandGateway.send(command, MetaData.with("promobookingid", event.getPromoBookingId()));
        log.info("Send booking command for room: '{}'", roomNumber);
    }

    @SneakyThrows
    boolean isVacant(int roomNumber, Instant startDate, Instant endDate) {
        RoomAvailabilityResponseData data =
                queryGateway.query(new FindRoomAvailability(roomNumber), RoomAvailabilityResponseData.class).get();
        return data.getBookings().stream().noneMatch(booking -> booking.getStartDate().isBefore(endDate)
                && booking.getEndDate().isAfter(startDate));
    }
}
