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
