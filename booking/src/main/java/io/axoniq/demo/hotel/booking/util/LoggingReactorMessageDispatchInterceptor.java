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

import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.messaging.ReactorMessageDispatchInterceptor;
import org.axonframework.messaging.Message;
import reactor.core.publisher.Mono;

@Slf4j
public class LoggingReactorMessageDispatchInterceptor<M extends Message<?>>
        implements ReactorMessageDispatchInterceptor<M> {

    @SuppressWarnings("unchecked")
    @Override
    public Mono<M> intercept(Mono<M> monoMessage) {
        return monoMessage.doOnNext(message -> log
                .info("Dispatched message: [{}]", message.getPayloadType().getSimpleName()));
    }
}
