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

package io.axoniq.demo.hotel.booking.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@ProcessingGroup("room-availability")
class RoomAvailabilityHandler {

    private final RoomAvailabilityEntityRepository roomAvailabilityEntityRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    private RoomAvailabilityResponseData convert(RoomAvailabilityEntity entity, UUID accountId) {
        return new RoomAvailabilityResponseData(entity.getRoomNumber(),
                                                entity.getRoomDescription(),
                                                entity.getRoomStatus(),
                                                accountId==null ? entity.getBookings().stream().map(it -> new BookingData(it.getId(), it.getStartDate(), it.getEndDate())).collect(Collectors.toUnmodifiableList()) : entity.getBookings().stream().filter(it -> it.getAccountId().equals(accountId.toString())).map(it -> new BookingData(it.getId(), it.getStartDate(), it.getEndDate())).collect(Collectors.toUnmodifiableList()),
                                                accountId==null ? Collections.emptyList() : entity.getFailedBookings().stream().filter(it -> it.getAccountId().equals(accountId.toString())).map(it -> new FailedBookingData(it.getStartDate(), it.getEndDate(), it.getReason())).collect(Collectors.toUnmodifiableList()));
    }

    RoomAvailabilityHandler(RoomAvailabilityEntityRepository roomAvailabilityEntityRepository, QueryUpdateEmitter queryUpdateEmitter) {
        this.roomAvailabilityEntityRepository = roomAvailabilityEntityRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    void on(RoomAddedEvent event) {
        RoomAvailabilityEntity entity = new RoomAvailabilityEntity(event.getRoomNumber(),
                                                                   event.getRoomDescription(),
                                                                   RoomStatus.EMPTY,
                                                                   Collections.emptyList(),
                                                                   Collections.emptyList());
        roomAvailabilityEntityRepository.save(entity);
        /* sending it to subscription queries of type FindRoomAvailability, but only if room id matches. */
        queryUpdateEmitter.emit(FindRoomAvailability.class,
                                query -> query.getRoomId() == event.getRoomNumber(),
                                convert(entity, null));
    }

    @EventHandler
    void on(RoomBookedEvent event, RoomAvailabilityEntity entity) {
        entity.setRoomStatus(RoomStatus.BOOKED);
        entity.getBookings().add(new BookingEmbeddable(event.getRoomBooking().getBookingId().toString(),
                                                       event.getRoomBooking().getStartDate(),
                                                       event.getRoomBooking().getEndDate(),
                                                       event.getRoomBooking().getAccountID().toString()));
        roomAvailabilityEntityRepository.save(entity);

        /* sending it to subscription queries of type FindRoomAvailabilityForAccount, but only if account id and room id matches. */
        queryUpdateEmitter.emit(FindRoomAvailabilityForAccount.class,
                                query -> query.getAccountId().equals(event.getRoomBooking().getAccountID()) && query.getRoomId() == event.getRoomNumber(),
                                convert(entity, event.getRoomBooking().getAccountID()));

        /* sending it to subscription queries of type FindRoomAvailability, but only if room id matches. */
        queryUpdateEmitter.emit(FindRoomAvailability.class,
                                query -> query.getRoomId() == event.getRoomNumber(),
                                convert(entity, null));
    }

    @EventHandler
    void on(RoomBookingRejectedEvent event) {
        RoomAvailabilityEntity entity = roomAvailabilityEntityRepository.getById(event.getRoomNumber());
        entity.getFailedBookings().add(new FailedBookingEmbeddable(
                event.getRoomBooking().getBookingId().toString(),
                event.getRoomBooking().getStartDate(),
                event.getRoomBooking().getEndDate(),
                event.getRoomBooking().getAccountID().toString(),
                event.getReason()));
        roomAvailabilityEntityRepository.save(entity);

        /* sending it to subscription queries of type FindRoomAvailabilityForAccount, but only if account id and room id matches. */
        queryUpdateEmitter.emit(FindRoomAvailabilityForAccount.class,
                                query -> query.getAccountId().equals(event.getRoomBooking().getAccountID()) && query.getRoomId() == event.getRoomNumber(),
                                convert(entity, event.getRoomBooking().getAccountID()));

        /* sending it to subscription queries of type FindRoomAvailability, but only if room id matches. */
        queryUpdateEmitter.emit(FindRoomAvailability.class,
                                query -> query.getRoomId() == event.getRoomNumber(),
                                convert(entity, null));
    }

    @EventHandler
    void on(RoomCheckedOutEvent event) {
        RoomAvailabilityEntity entity = roomAvailabilityEntityRepository.getById(event.getRoomNumber());
        UUID accountId = entity.getBookings().stream().filter(it -> it.getId().equals(event.getRoomBookingId().toString())).map(it -> UUID.fromString(it.getAccountId())).findAny().orElseThrow();

        entity.setBookings(entity.getBookings().stream().filter(it -> !it.getId().equals(event.getRoomBookingId().toString())).collect(Collectors.toList()));
        entity.setRoomStatus(RoomStatus.EMPTY);
        roomAvailabilityEntityRepository.save(entity);

        /* sending it to subscription queries of type FindRoomAvailabilityForAccount, but only if account id and room id matches. */
        queryUpdateEmitter.emit(FindRoomAvailabilityForAccount.class,
                                query -> query.getAccountId().equals(accountId) && query.getRoomId() == event.getRoomNumber(),
                                convert(entity, accountId));

        /* sending it to subscription queries of type FindRoomAvailability, but only if room id matches. */
        queryUpdateEmitter.emit(FindRoomAvailability.class,
                                query -> query.getRoomId() == event.getRoomNumber(),
                                convert(entity, null));
    }

    @QueryHandler
    RoomAvailabilityResponseData handle(FindRoomAvailabilityForAccount query) {
        return convert(roomAvailabilityEntityRepository.getById(query.getRoomId()), query.getAccountId());
    }

    @QueryHandler
    RoomAvailabilityResponseData handle(FindRoomAvailability query) {
        return convert(roomAvailabilityEntityRepository.getById(query.getRoomId()), null);
    }
}
