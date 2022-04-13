package io.axoniq.demo.hotel.inventory.command;

import io.axoniq.demo.hotel.inventory.command.api.CreateRoomCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutomaticMultiCommandDispatcher {
    final Random random = new Random();
    private final CommandGateway commandGateway;

    @Scheduled(fixedRate = 5000)
    public void send() {
        log.info("Sending multicommand");
        commandGateway.sendAndWait(new MultiCommand(), 30, TimeUnit.SECONDS);
        log.info("Completed multicommand");
    }

    @CommandHandler
    public void on(MultiCommand command) {
        final UUID id = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();
        commandGateway.sendAndWait(new CreateRoomCommand(id, random.nextInt(), "Some description"));
        commandGateway.sendAndWait(new CreateRoomCommand(id2, random.nextInt(), "Some description"));
    }
}
