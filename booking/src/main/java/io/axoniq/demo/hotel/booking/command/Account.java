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

package io.axoniq.demo.hotel.booking.command;

import io.axoniq.demo.hotel.booking.command.api.AccountRegisteredEvent;
import io.axoniq.demo.hotel.booking.command.api.PayCommand;
import io.axoniq.demo.hotel.booking.command.api.RegisterAccountCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate(snapshotTriggerDefinition = "accountSnapshotTriggerDefinition", cache = "cache")
class Account {
    @AggregateIdentifier
    private UUID accountId;

    private Account() {
    }

    @CommandHandler
    Account(RegisterAccountCommand command) {
        apply(new AccountRegisteredEvent(command.getAccountId(), command.getUserName(), command.getPassword()));
    }

    @CommandHandler
    void on(PayCommand command) throws Exception {
        AggregateLifecycle.createNew(Payment.class, () -> new Payment(command));
    }

    @EventSourcingHandler
    void on(AccountRegisteredEvent event) {
        this.accountId = event.getAccountId();
    }
}
