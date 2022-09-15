package io.axoniq.demo.hotel.booking.query;

import io.axoniq.demo.hotel.booking.command.api.RoomEvent;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@Component
public class RoomAvailabilityEntityResolver implements ParameterResolver<RoomAvailabilityEntity>,
        ParameterResolverFactory {

    private final RoomAvailabilityEntityRepository roomAvailabilityEntityRepository;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public RoomAvailabilityEntityResolver(RoomAvailabilityEntityRepository roomAvailabilityEntityRepository) {
        this.roomAvailabilityEntityRepository = roomAvailabilityEntityRepository;
    }

    @Override
    public RoomAvailabilityEntity resolveParameterValue(Message<?> message) {
        Integer roomNumber = ((RoomEvent) message.getPayload()).getRoomNumber();
        return roomAvailabilityEntityRepository.getById(roomNumber);
    }

    @Override
    public boolean matches(Message<?> message) {
        return RoomEvent.class.isAssignableFrom(message.getPayloadType());
    }

    @Override
    public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int parameterIndex) {
        if (RoomAvailabilityEntity.class.equals(parameters[parameterIndex].getType())) {
            return this;
        }
        return null;
    }
}
