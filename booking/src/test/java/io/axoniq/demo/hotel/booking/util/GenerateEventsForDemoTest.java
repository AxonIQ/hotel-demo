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

import static io.axoniq.demo.hotel.booking.util.GenerateEventsForDemo.DOUBLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION;
import static io.axoniq.demo.hotel.booking.util.GenerateEventsForDemo.DOUBLE_ROOM_SEA_SIDE_DESCRIPTION;
import static io.axoniq.demo.hotel.booking.util.GenerateEventsForDemo.SINGLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION;
import static io.axoniq.demo.hotel.booking.util.GenerateEventsForDemo.SINGLE_ROOM_SEA_SIDE_DESCRIPTION;
import static io.axoniq.demo.hotel.booking.util.GenerateEventsForDemo.TOTAL_BOOKING_AMOUNT;
import static io.axoniq.demo.hotel.booking.util.GenerateEventsForDemo.USER_NAME_FORMAT;
import static io.axoniq.demo.hotel.booking.util.GenerateEventsForDemo.USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import io.axoniq.demo.hotel.booking.command.api.AddRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.BookRoomCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckInCommand;
import io.axoniq.demo.hotel.booking.command.api.CheckOutCommand;
import io.axoniq.demo.hotel.booking.command.api.MarkRoomAsPreparedCommand;
import io.axoniq.demo.hotel.booking.command.api.PayCommand;
import io.axoniq.demo.hotel.booking.command.api.ProcessPaymentCommand;
import io.axoniq.demo.hotel.booking.command.api.RegisterAccountCommand;
import io.axoniq.demo.hotel.booking.query.RoomAvailabilityEntityRepository;

public class GenerateEventsForDemoTest {
    private static int NUMBER_OF_SEND_COMMANDS = 58;
    private static int NUMBER_OF_SEND_AND_WAIT_COMMANDS = 9;

    private static final int NUMBER_OF_ADD_ROOM_COMMANDS = 20;
    private static final int NUMBER_OF_PAY_COMMANDS = 9;
    private static final int NUMBER_OF_REGISTER_ACCOUNT_COMMANDS = 9;
    private static final int NUMBER_OF_BOOK_ROOM_COMMANDS = 5;
    private static final int NUMBER_OF_CHECK_OUT_COMMANDS = 5;
    private static final int NUMBER_OF_MARK_ROOM_AS_PREPARED_COMMANDS = 5;
    private static final int NUMBER_OF_CHECK_IN_COMMANDS = 5;
    private static final int NUMBER_OF_PROCESS_PAYMENT_COMMANDS = 9;

    private CommandGateway commandGateway = mock(CommandGateway.class);
    private RoomAvailabilityEntityRepository roomAvailabilityEntityRepository = mock(RoomAvailabilityEntityRepository.class);
    private UUIDProvider uuidProvider = mock(UUIDProvider.class);
    private UUID accountId = UUID.randomUUID();
    private UUID bookingId = UUID.randomUUID();
    private UUID paymentId = UUID.randomUUID();
    @InjectMocks
    private GenerateEventsForDemo generateEventsForDemo;

    @BeforeEach
    void setUp() {
        generateEventsForDemo = new GenerateEventsForDemo(commandGateway, roomAvailabilityEntityRepository,
                                                          uuidProvider);
        when(uuidProvider.getAccountId()).thenReturn(accountId);
        when(uuidProvider.getBookingId()).thenReturn(bookingId);
        when(uuidProvider.getPaymentId()).thenReturn(paymentId);
    }

    @Test
    void sendCommandsForBookingTest(){
        generateEventsForDemo.sendCommandsForBooking();
        verifySendCommands();
        verifySendAndWaitCommands();
    }

    @Test
    void doNotAddRoomsWhenRoomsAlreadyExist(){
        when(roomAvailabilityEntityRepository.existsById(100)).thenReturn(true);
        generateEventsForDemo.sendCommandsForBooking();
        ArgumentCaptor<Object> commandSendCaptor = ArgumentCaptor.forClass(Object.class);
        verify(commandGateway, times(38)).send(commandSendCaptor.capture());
       assertEquals(0L, commandSendCaptor.getAllValues().stream().filter(o -> o instanceof AddRoomCommand).count());
    }

    private void verifySendCommands() {
        ArgumentCaptor<Object> commandSendCaptor = ArgumentCaptor.forClass(Object.class);
        verify(commandGateway, times(NUMBER_OF_SEND_COMMANDS)).send(commandSendCaptor.capture());

        List<AddRoomCommand> addRoomCommandList = new ArrayList<>();
        List<RegisterAccountCommand> registerAccountCommandList = new ArrayList<>();
        List<BookRoomCommand> bookRoomCommandList = new ArrayList<>();
        List<CheckOutCommand> checkOutCommandList = new ArrayList<>();
        List<ProcessPaymentCommand> processPaymentCommands = new ArrayList<>();
        List<MarkRoomAsPreparedCommand> markRoomAsPreparedCommandList = new ArrayList<>();
        List<CheckInCommand> checkInCommandList = new ArrayList<>();

        for (Object command : commandSendCaptor.getAllValues()){
            if (command instanceof AddRoomCommand){
                addRoomCommandList.add((AddRoomCommand) command);
            }
            if (command instanceof RegisterAccountCommand){
                registerAccountCommandList.add((RegisterAccountCommand) command);
            }
            if (command instanceof BookRoomCommand){
                bookRoomCommandList.add((BookRoomCommand) command);
            }
            if (command instanceof CheckOutCommand){
                checkOutCommandList.add((CheckOutCommand) command);
            }
            if (command instanceof ProcessPaymentCommand){
                processPaymentCommands.add((ProcessPaymentCommand) command);
            }
            if (command instanceof MarkRoomAsPreparedCommand){
                markRoomAsPreparedCommandList.add((MarkRoomAsPreparedCommand) command);
            }
            if (command instanceof CheckInCommand){
                checkInCommandList.add((CheckInCommand) command);
            }

        }
        assertEquals(NUMBER_OF_ADD_ROOM_COMMANDS, addRoomCommandList.size());
        assertEquals(NUMBER_OF_REGISTER_ACCOUNT_COMMANDS, registerAccountCommandList.size());
        assertEquals(NUMBER_OF_BOOK_ROOM_COMMANDS, bookRoomCommandList.size());
        assertEquals(NUMBER_OF_CHECK_OUT_COMMANDS, checkOutCommandList.size());
        assertEquals(NUMBER_OF_PROCESS_PAYMENT_COMMANDS, processPaymentCommands.size());
        assertEquals(NUMBER_OF_MARK_ROOM_AS_PREPARED_COMMANDS, markRoomAsPreparedCommandList.size());
        assertEquals(NUMBER_OF_CHECK_IN_COMMANDS, checkInCommandList.size());

        checkAddRoomCommands(addRoomCommandList);
        checkRegisterAccountCommands(registerAccountCommandList);
        IntStream.range(100, 105).forEach(roomNumber -> checkBookRoomCommands(bookRoomCommandList, roomNumber));
        IntStream.range(100, 105).forEach(roomNumber -> checkMarkRoomAsPreparedCommand(markRoomAsPreparedCommandList, roomNumber));
        IntStream.range(100, 105).forEach(roomNumber -> checkCheckInCommand(checkInCommandList, roomNumber));
        IntStream.range(100, 105).forEach(roomNumber -> checkCheckOutCommand(checkOutCommandList, roomNumber));
        checkProcessPaymentCommands(processPaymentCommands);
    }

    private void verifySendAndWaitCommands() {
        ArgumentCaptor<Object> commandSendAndWaitCaptor = ArgumentCaptor.forClass(Object.class);
        verify(commandGateway, times(NUMBER_OF_SEND_AND_WAIT_COMMANDS)).sendAndWait(commandSendAndWaitCaptor.capture());


        List<PayCommand> payCommandList = new ArrayList<>();

        for (Object command: commandSendAndWaitCaptor.getAllValues()){

            if (command instanceof PayCommand){
                payCommandList.add((PayCommand) command);
            }
        }
        assertEquals(NUMBER_OF_PAY_COMMANDS, payCommandList.size());
        checkPayCommands(payCommandList);
    }


    private void checkAddRoomCommands(List<AddRoomCommand> addRoomCommandsList) {
        IntStream.range(100, 105).forEach(roomNumber -> checkAddRoomCommand(addRoomCommandsList, roomNumber, SINGLE_ROOM_SEA_SIDE_DESCRIPTION));
        IntStream.range(105, 110).forEach(roomNumber -> checkAddRoomCommand(addRoomCommandsList, roomNumber, SINGLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION));
        IntStream.range(200, 205).forEach(roomNumber -> checkAddRoomCommand(addRoomCommandsList, roomNumber, DOUBLE_ROOM_SEA_SIDE_DESCRIPTION));
        IntStream.range(205, 210).forEach(roomNumber -> checkAddRoomCommand(addRoomCommandsList, roomNumber, DOUBLE_ROOM_MOUNTAIN_SIDE_DESCRIPTION));
    }

    private void checkAddRoomCommand(List<AddRoomCommand> addRoomCommandsList, int roomNumber, String roomDescription){
        assertTrue(addRoomCommandsList.contains(new AddRoomCommand(roomNumber, roomDescription)));
    }

    private void checkRegisterAccountCommands(List<RegisterAccountCommand> registerAccountCommandList){
        assertTrue(registerAccountCommandList.contains(new RegisterAccountCommand(accountId,String.format(USER_NAME_FORMAT,
                                                                                                      accountId), USER_PASSWORD)));
    }

    private void checkBookRoomCommands(List<BookRoomCommand> bookRoomCommandList, int roomNumber){
        assertTrue(bookRoomCommandList.stream().anyMatch(bookRoomCommand -> checkBookRoomCommand(bookRoomCommand, roomNumber)));
    }

    private boolean checkBookRoomCommand(BookRoomCommand bookRoomCommand, int roomNumber) {
        return bookRoomCommand.getRoomNumber() == roomNumber && bookRoomCommand.getRoomBooking().getAccountID().equals(accountId) && bookRoomCommand.getRoomBooking().getBookingId().equals(bookingId);
    }

    private void  checkMarkRoomAsPreparedCommand(List<MarkRoomAsPreparedCommand> markRoomAsPreparedCommandList, int roomNumber){
        assertTrue(markRoomAsPreparedCommandList.contains(new MarkRoomAsPreparedCommand(roomNumber, bookingId)));
    }
    private void  checkCheckInCommand(List<CheckInCommand> checkInCommandList, int roomNumber){
        assertTrue(checkInCommandList.contains(new CheckInCommand(roomNumber, bookingId)));
    }

    private void  checkCheckOutCommand(List<CheckOutCommand> checkOutCommandList, int roomNumber){
        assertTrue(checkOutCommandList.contains(new CheckOutCommand(roomNumber, bookingId)));
    }

    private void checkPayCommands(List<PayCommand> payCommandList){
        assertTrue(payCommandList.contains(new PayCommand(paymentId, accountId, TOTAL_BOOKING_AMOUNT)));
    }
    private void checkProcessPaymentCommands(List<ProcessPaymentCommand> processPaymentCommandList){
        assertTrue(processPaymentCommandList.contains(new ProcessPaymentCommand(paymentId)));
    }
}
