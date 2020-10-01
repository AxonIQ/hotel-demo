package io.axoniq.demo.hotel.booking.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, String> {
    List<PaymentEntity> findByAccountId(UUID accountId);
}
