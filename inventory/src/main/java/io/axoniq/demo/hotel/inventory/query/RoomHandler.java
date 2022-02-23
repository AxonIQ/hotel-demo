/*
 * Copyright (c) 2020-2022. AxonIQ
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

package io.axoniq.demo.hotel.inventory.query;

import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToBookingSystemEvent;
import io.axoniq.demo.hotel.inventory.command.api.RoomAddedToInventoryEvent;
import io.axoniq.demo.hotel.inventory.command.api.RoomCreatedEvent;
import io.axoniq.demo.hotel.inventory.command.web.api.RoomOverviewData;
import io.axoniq.demo.hotel.inventory.query.api.FindRooms;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ProcessingGroup("room")
class RoomHandler {
    private final RoomEntityRepository roomEntityRepository;

    RoomHandler(RoomEntityRepository roomEntityRepository) {
        this.roomEntityRepository = roomEntityRepository;
    }

    @EventHandler
    void on(RoomCreatedEvent event) {
        RoomEntity entity = new RoomEntity(event.getRoomId().toString(), event.getRoomNumber(), event.getRoomDescription(), false, false);
        this.roomEntityRepository.save(entity);
    }

    @EventHandler
    void on(RoomAddedToInventoryEvent event) {
        RoomEntity entity = this.roomEntityRepository.getById(event.getRoomId().toString());
        entity.setAddedToInventory(true);
        this.roomEntityRepository.save(entity);
    }

    @EventHandler
    void on(RoomAddedToBookingSystemEvent event) {
        RoomEntity entity = this.roomEntityRepository.getById(event.getRoomId().toString());
        entity.setAddedToBooking(true);
        this.roomEntityRepository.save(entity);
    }

    @QueryHandler
    List<RoomOverviewData> on(FindRooms query) {
        return this.roomEntityRepository
                .findAll()
                .stream()
                .map(RoomHandler::convert)
                .collect(Collectors.toList());
    }

    private static RoomOverviewData convert(RoomEntity entity) {
        return new RoomOverviewData(entity.getRoomId(), entity.getRoomNumber(), entity.getDescription(), entity.getAddedToInventory(), entity.getAddedToBooking());
    }
}
