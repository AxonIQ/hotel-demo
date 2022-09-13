package io.axoniq.demo.hotel.booking.query;

import io.axoniq.demo.hotel.booking.command.api.RoomEvent;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.annotation.ParameterResolver;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@Component
public class RoomAvailabilityEntityResolver implements ParameterResolver<RoomAvailabilityEntity>,
        ParameterResolverFactory {

    private final RoomAvailabilityEntityRepository roomAvailabilityEntityRepository;

    @Autowired
    public RoomAvailabilityEntityResolver(RoomAvailabilityEntityRepository roomAvailabilityEntityRepository) {
        this.roomAvailabilityEntityRepository = roomAvailabilityEntityRepository;
    }

    @Override
    public RoomAvailabilityEntity resolveParameterValue(Message<?> message) {
        if (matches(message)) {
            Integer roomNumber = ((RoomEvent) message.getPayload()).getRoomNumber();
            return roomAvailabilityEntityRepository.getById(roomNumber);
        }
        return null;
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
