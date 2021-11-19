package io.axoniq.demo.hotel.booking.transformer;

import io.axoniq.axonserver.util.EventTransformer;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class HotelEventTransformer implements EventTransformer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Serializer serializer = JacksonSerializer.defaultSerializer();

    @Override
    public boolean canApply(SerializedObject<byte[]> serializedObject) {
        return serializedObject.getType().getName().equals("io.axoniq.demo.hotel.booking.command.api.RoomAddedEvent");
    }

    @Override
    public SerializedObject<byte[]> apply(SerializedObject<byte[]> serializedObject) {
        Object x = serializer.deserialize(serializedObject);
        logger.info(String.format("Deserialized event: %s" ,x));
        return serializedObject;
    }
}
