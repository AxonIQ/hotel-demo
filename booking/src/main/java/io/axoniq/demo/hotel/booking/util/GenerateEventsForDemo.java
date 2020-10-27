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
package io.axoniq.demo.hotel.booking.util;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.axoniq.demo.hotel.booking.command.api.AddRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.BookRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckInCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckOutCommand;
import io.axoniq.demo.hotel.booking.command.api.MarkRoomAsPreparedCommand;
import io.axoniq.demo.hotel.booking.command.api.PayCommand;
import io.axoniq.demo.hotel.booking.command.api.ProcessPaymentCommand;
import io.axoniq.demo.hotel.booking.command.api.RegisterAccountCommand;
import io.axoniq.demo.hotel.booking.command.api.RoomBooking;
import io.axoniq.demo.hotel.booking.query.RoomAvailabilityEntityRepository;


@Component
@Profile("demo")
public class GenerateEventsForDemo {

    public static final BigDecimal TOTAL_BOOKING_AMOUNT = new BigDecimal(1000);
    public static final String USER_NAME_FORMAT = "guest-%s";
    public static final String USER_PASSWORD = "Welcome1";
    public static final String SINGLE_ROOM_SEA_SIDE_DESCRIPTION = "Single Room Sea side";
    public static final String SINGLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION = "Single Room Mountain side";
    public static final String DOUBLE_ROOM_SEA_SIDE_DESCRIPTION = "Double Room Sea side";
    public static final String DOUBLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION = "Double Room Mountain side";

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CommandGateway commandGateway;
    private final RoomAvailabilityEntityRepository roomAvailabilityEntityRepository;
    private final UUIDProvider uuidProvider;

    public GenerateEventsForDemo(CommandGateway commandGateway,
                                 RoomAvailabilityEntityRepository roomAvailabilityEntityRepository,
                                 UUIDProvider uuidProvider) {
        this.commandGateway = commandGateway;
        this.roomAvailabilityEntityRepository = roomAvailabilityEntityRepository;
        this.uuidProvider = uuidProvider;
    }

    // Sends commands to Booking context every 5 minutes
    @Scheduled(cron = "0 */5 * ? * *")
    public void sendCommandsForBooking() {
        {
            logger.info("Started to automatically send commands to Booking context");
            if (!roomAvailabilityEntityRepository.existsById(100)) {
                addRooms();
            }
            List<UUID> accountIds = registerAccounts();
            Map<Integer, UUID> roomBookingIdMap = bookRooms(accountIds);
            markRoomAsPrepared(roomBookingIdMap);
            checkIn(roomBookingIdMap);
            checkOut(roomBookingIdMap);
            processPayment(accountIds);
            logger.info("Finished with automatically sending commands to Booking context");
        }
    }

    private void processPayment(List<UUID> accountIds) {
        accountIds.forEach(accountId -> {
                               UUID paymentId = uuidProvider.getPaymentId();
                               commandGateway.sendAndWait(new PayCommand(paymentId, accountId, TOTAL_BOOKING_AMOUNT));
                               commandGateway.send(new ProcessPaymentCommand(paymentId));
                           }
        );
    }

    private void markRoomAsPrepared(Map<Integer, UUID> roomBookingIdMap) {
        roomBookingIdMap.forEach((roomNumber, bookingId) ->
                                         commandGateway.send(new MarkRoomAsPreparedCommand(roomNumber, bookingId)));
    }

    private void checkIn(Map<Integer, UUID> roomBookingIdMap) {
        roomBookingIdMap.forEach((roomNumber, bookingId) ->
                                     commandGateway.send(new CheckInCommand(roomNumber, bookingId)));
    }

    private void checkOut(Map<Integer, UUID> roomBookingIdMap) {
        roomBookingIdMap.forEach((roomNumber, bookingId) ->
                                         commandGateway.send(new CheckOutCommand(roomNumber, bookingId)));
    }

    private Map<Integer, UUID> bookRooms(List<UUID> accountIds) {
        Map<Integer, UUID> roomBookingIdMap = new HashMap<>();
        IntStream.range(0, 5).forEach(index -> {
                                          Integer startBookingAfterDays = Math.toIntExact(Math.round(Math.random() * 1000.0));
                                          Instant startBooking = Instant.now().plus(startBookingAfterDays, ChronoUnit.DAYS);
                                          Instant endBooking = startBooking.plus(3, ChronoUnit.DAYS);
                                          UUID bookingId = uuidProvider.getBookingId();
                                          RoomBooking roomBooking =
                                                  new RoomBooking(startBooking, endBooking, accountIds.get(index), bookingId);
                                          final int roomNumber = index + 100;
                                          commandGateway.send(new BookRoomCommand(roomNumber, roomBooking));
                                          roomBookingIdMap.put(roomNumber, bookingId);
                                      }
        );
        return roomBookingIdMap;
    }

    private List<UUID> registerAccounts() {
        List<UUID> accountIds = new ArrayList<>();
        IntStream.range(0, 9).forEach(index -> {
                                          UUID accountId = uuidProvider.getAccountId();
                                          registerAccount(accountId);
                                          accountIds.add(accountId);
                                      }
        );
        return accountIds;
    }

    private void registerAccount(UUID accountId) {
        RegisterAccountCommand registerAccountCommand = new RegisterAccountCommand(accountId,
                                                                                   String.format(USER_NAME_FORMAT,
                                                                                                 accountId),
                                                                                   USER_PASSWORD);
        commandGateway.send(registerAccountCommand);
    }

    private void addRooms() {
        IntStream.range(100, 105).forEach(
                roomNumber -> addRoom(roomNumber, SINGLE_ROOM_SEA_SIDE_DESCRIPTION));
        IntStream.range(105, 110).forEach(
                roomNumber -> addRoom(roomNumber, SINGLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION));
        IntStream.range(200, 205).forEach(
                roomNumber -> addRoom(roomNumber, DOUBLE_ROOM_SEA_SIDE_DESCRIPTION));
        IntStream.range(205, 210).forEach(
                roomNumber -> addRoom(roomNumber, DOUBLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION));
    }

    private void addRoom(int roomNumber, String roomDescription) {
        commandGateway.send(new AddRoomCommand(roomNumber,
                                               roomDescription));
    }
}

