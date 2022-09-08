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

import io.axoniq.demo.hotel.booking.command.api.RoomBookedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomCheckedInEvent;
import io.axoniq.demo.hotel.booking.query.api.BookingData;
import io.axoniq.demo.hotel.booking.query.api.FindAllRoomCheckoutSchedules;
import io.axoniq.demo.hotel.booking.query.api.RoomCheckoutScheduleData;
import io.axoniq.demo.hotel.booking.query.api.RoomStatus;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ProcessingGroup("room-checkout")
class RoomCheckoutScheduleHandler {

    private final RoomCheckoutScheduleRepository roomCheckoutScheduleRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    private RoomCheckoutScheduleData convert(RoomCheckoutScheduleEntity entity) {
        return new RoomCheckoutScheduleData(entity.getRoomNumber(), entity.getBookings().stream().map(it -> new BookingData(it.getId(), it.getStartDate(), it.getEndDate())).collect(Collectors.toList()));
    }

    RoomCheckoutScheduleHandler(RoomCheckoutScheduleRepository roomCheckoutScheduleRepository, QueryUpdateEmitter queryUpdateEmitter) {
        this.roomCheckoutScheduleRepository = roomCheckoutScheduleRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    void on(RoomBookedEvent event) {
        Optional<RoomCheckoutScheduleEntity> optionalEntity = this.roomCheckoutScheduleRepository.findById(event.getRoomNumber());
        RoomCheckoutScheduleEntity entityToSave;
        if (optionalEntity.isPresent()){
            entityToSave=optionalEntity.get();
            entityToSave.getBookings().add(new BookingEmbeddable(event.getRoomBooking().getBookingId().toString(), event.getRoomBooking().getStartDate(), event.getRoomBooking().getEndDate(), event.getRoomBooking().getAccountID().toString()));
        } else {
            entityToSave = new RoomCheckoutScheduleEntity(event.getRoomNumber(), RoomStatus.BOOKED, Collections.singletonList(new BookingEmbeddable(event.getRoomBooking().getBookingId().toString(), event.getRoomBooking().getStartDate(), event.getRoomBooking().getEndDate(), event.getRoomBooking().getAccountID().toString())));
        }
        this.roomCheckoutScheduleRepository.saveAndFlush(entityToSave);

        /* sending it to subscription queries of type FindAllRoomCleaningSchedules. */
        queryUpdateEmitter.emit(FindAllRoomCheckoutSchedules.class,
                                query -> true,
                                convert(entityToSave));
    }

    @EventHandler
    void on(RoomCheckedInEvent event) {
        RoomCheckoutScheduleEntity entity = this.roomCheckoutScheduleRepository.getById(event.getRoomNumber());
        entity.setRoomStatus(RoomStatus.TAKEN);
        this.roomCheckoutScheduleRepository.save(entity);

        /* sending it to subscription queries of type FindAllRoomCleaningSchedules. */
        queryUpdateEmitter.emit(FindAllRoomCheckoutSchedules.class,
                                query -> true,
                                convert(entity));
    }

    @QueryHandler
    List<RoomCheckoutScheduleData> handle(FindAllRoomCheckoutSchedules query) {
        return this.roomCheckoutScheduleRepository.findAll().stream().filter(roomCheckoutScheduleEntity -> roomCheckoutScheduleEntity.getRoomStatus()==RoomStatus.TAKEN).map(this::convert).collect(Collectors.toList());
    }
}
