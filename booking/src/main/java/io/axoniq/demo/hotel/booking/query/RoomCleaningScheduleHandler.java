package io.axoniq.demo.hotel.booking.query;

import io.axoniq.demo.hotel.booking.command.api.RoomBookedEvent;
import io.axoniq.demo.hotel.booking.command.api.RoomPreparedEvent;
import io.axoniq.demo.hotel.booking.query.api.FindAllRoomCleaningSchedules;
import io.axoniq.demo.hotel.booking.query.api.RoomCleaningScheduleData;
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
@ProcessingGroup("room-cleaning")
class RoomCleaningScheduleHandler {

    private final RoomCleaningScheduleRepository roomCleaningScheduleRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    private RoomCleaningScheduleData convert(RoomCleaningScheduleEntity entity) {
        return new RoomCleaningScheduleData(entity.getRoomNumber(), entity.getBookings().stream().map(BookingEmbeddable::getStartDate).collect(Collectors.toList()));
    }

    RoomCleaningScheduleHandler(RoomCleaningScheduleRepository roomCleaningScheduleRepository, QueryUpdateEmitter queryUpdateEmitter) {
        this.roomCleaningScheduleRepository = roomCleaningScheduleRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    void on(RoomBookedEvent event) {
        Optional<RoomCleaningScheduleEntity> optionalEntity = this.roomCleaningScheduleRepository.findById(event.getRoomNumber());
        RoomCleaningScheduleEntity entityToSave;
        if (optionalEntity.isPresent()){
            entityToSave=optionalEntity.get();
            entityToSave.getBookings().add(new BookingEmbeddable(event.getRoomBooking().getBookingId().toString(), event.getRoomBooking().getStartDate(), event.getRoomBooking().getEndDate(), event.getRoomBooking().getAccountID().toString()));
        } else {
            entityToSave = new RoomCleaningScheduleEntity(event.getRoomNumber(), Collections.singletonList(new BookingEmbeddable(event.getRoomBooking().getBookingId().toString(), event.getRoomBooking().getStartDate(), event.getRoomBooking().getEndDate(), event.getRoomBooking().getAccountID().toString())));
        }
        this.roomCleaningScheduleRepository.saveAndFlush(entityToSave);

        /* sending it to subscription queries of type FindAllRoomCleaningSchedules. */
        queryUpdateEmitter.emit(FindAllRoomCleaningSchedules.class,
                                query -> true,
                                convert(entityToSave));
    }

    @EventHandler
    void on(RoomPreparedEvent event) {
        RoomCleaningScheduleEntity entity = this.roomCleaningScheduleRepository.getOne(event.getRoomNumber());
        entity.setBookings(entity.getBookings().stream().filter(it -> !it.getId().equals(event.getRoomBooking().getBookingId().toString())).collect(Collectors.toList()));
        this.roomCleaningScheduleRepository.save(entity);

        /* sending it to subscription queries of type FindAllRoomCleaningSchedules. */
        queryUpdateEmitter.emit(FindAllRoomCleaningSchedules.class,
                                query -> true,
                                convert(entity));
    }

    @QueryHandler
    List<RoomCleaningScheduleData> handle(FindAllRoomCleaningSchedules query) {
        return this.roomCleaningScheduleRepository.findAll().stream().map(this::convert).collect(Collectors.toList());
    }
}
