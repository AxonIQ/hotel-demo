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

package io.axoniq.demo.hotel.inventory.query.web.rest;

import io.axoniq.demo.hotel.inventory.command.web.api.RoomOverviewData;
import io.axoniq.demo.hotel.inventory.query.api.FindRooms;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@CrossOrigin
class RoomQueryController {

    private final ReactorQueryGateway reactorQueryGateway;

    public RoomQueryController(ReactorQueryGateway reactorQueryGateway) {
        this.reactorQueryGateway = reactorQueryGateway;
    }

    @GetMapping(path = "/rooms")
    public Mono<List<RoomOverviewData>> allRooms() {
        return reactorQueryGateway.query(new FindRooms(), ResponseTypes.multipleInstancesOf(RoomOverviewData.class));
    }
}
