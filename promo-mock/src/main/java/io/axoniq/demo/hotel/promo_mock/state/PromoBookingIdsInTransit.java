package io.axoniq.demo.hotel.promo_mock.state;


import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class PromoBookingIdsInTransit {

    List<UUID> ids = Collections.synchronizedList(new ArrayList<>());

    public void add(UUID id) {
        ids.add(id);
    }

    public boolean remove(UUID id) {
        return ids.remove(id);
    }

    public Integer size() {
        return ids.size();
    }
}
