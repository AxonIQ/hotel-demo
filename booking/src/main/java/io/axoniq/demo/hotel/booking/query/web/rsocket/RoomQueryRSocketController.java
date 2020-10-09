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

package io.axoniq.demo.hotel.booking.query.web.rsocket;

import io.axoniq.demo.hotel.booking.query.api.FindAllRoomCleaningSchedules;
import io.axoniq.demo.hotel.booking.query.api.FindRoomAvailabilityForAccount;
import io.axoniq.demo.hotel.booking.query.api.RoomAvailabilityResponseData;
import io.axoniq.demo.hotel.booking.query.api.RoomCleaningScheduleData;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Controller
public class RoomQueryRSocketController {

    private final ReactorQueryGateway reactorQueryGateway;

    public RoomQueryRSocketController(ReactorQueryGateway reactorQueryGateway) {
        this.reactorQueryGateway = reactorQueryGateway;
    }

    // Request-Response
    @MessageMapping("rooms.{roomId}.account.{accountId}.availability")
    public Mono<RoomAvailabilityResponseData> roomAvailability(@DestinationVariable Integer roomId,
                                                               @DestinationVariable UUID accountId) {
        return reactorQueryGateway.query(new FindRoomAvailabilityForAccount(roomId, accountId),
                                         ResponseTypes.instanceOf(RoomAvailabilityResponseData.class));
    }

    // Request-Stream
    @MessageMapping("rooms.{roomId}.account.{accountId}.availability")
    public Flux<RoomAvailabilityResponseData> roomAvailability_subscribe(@DestinationVariable Integer roomId,
                                                                         @DestinationVariable UUID accountId) {
        return reactorQueryGateway.subscriptionQueryMany(new FindRoomAvailabilityForAccount(roomId, accountId),
                                                         RoomAvailabilityResponseData.class);
    }

    //#################################################################################################################

    // Request-Response
    @MessageMapping("rooms.cleaningschedule")
    public Mono<List<RoomCleaningScheduleData>> roomsCleaningSchedule() {
        return reactorQueryGateway.query(new FindAllRoomCleaningSchedules(),
                                         ResponseTypes.multipleInstancesOf(RoomCleaningScheduleData.class));
    }

    // Request-Stream
    @MessageMapping("rooms.cleaningschedule")
    public Flux<RoomCleaningScheduleData> roomsCleaningSchedule_subscribe() {
        return reactorQueryGateway
                .subscriptionQueryMany(new FindAllRoomCleaningSchedules(), RoomCleaningScheduleData.class);
    }
}
