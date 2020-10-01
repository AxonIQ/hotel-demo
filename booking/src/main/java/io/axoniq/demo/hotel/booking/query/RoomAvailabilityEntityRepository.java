package io.axoniq.demo.hotel.booking.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomAvailabilityEntityRepository extends JpaRepository<RoomAvailabilityEntity, Integer> {


}
