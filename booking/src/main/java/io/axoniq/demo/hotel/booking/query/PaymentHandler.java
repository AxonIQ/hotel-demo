package io.axoniq.demo.hotel.booking.query;

import io.axoniq.demo.hotel.booking.command.api.PaymentRequestedEvent;
import io.axoniq.demo.hotel.booking.command.api.PaymentSucceededEvent;
import io.axoniq.demo.hotel.booking.query.api.FindPayment;
import io.axoniq.demo.hotel.booking.query.api.FindPayments;
import io.axoniq.demo.hotel.booking.query.api.FindPaymentsForAccount;
import io.axoniq.demo.hotel.booking.query.api.PaymentResponseData;
import io.axoniq.demo.hotel.booking.query.api.PaymentStatus;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ProcessingGroup("payment")
class PaymentHandler {

    private final PaymentEntityRepository paymentEntityRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;


    private PaymentResponseData convert(PaymentEntity entity) {
        return new PaymentResponseData(entity.getPaymentId(), entity.getAccountId(), entity.getTotalAmount(), entity.getPaymentStatus());
    }

    PaymentHandler(PaymentEntityRepository paymentEntityRepository, QueryUpdateEmitter queryUpdateEmitter) {
        this.paymentEntityRepository = paymentEntityRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    void on(PaymentRequestedEvent event) {
        PaymentEntity entity = new PaymentEntity(event.getPaymentId().toString(), event.getAccountId(), event.getTotalAmount(), PaymentStatus.PROCESSING);
        this.paymentEntityRepository.save(entity);
        /* sending it to subscription queries of type FindPayment, but only if the payment id matches. */
        queryUpdateEmitter.emit(FindPayment.class,
                                query -> query.getPaymentId().equals(event.getPaymentId()),
                                convert(entity));
        /* sending it to subscription queries of type FindPaymentsForAccount, but only if account id matches. */
        queryUpdateEmitter.emit(FindPaymentsForAccount.class,
                                query -> query.getAccountId().equals(event.getAccountId()),
                                convert(entity));
        /* sending it to subscription queries of type FindPayments, every time. */
        queryUpdateEmitter.emit(FindPayments.class,
                                query -> true,
                                convert(entity));
    }

    @EventHandler
    void on(PaymentSucceededEvent event) {
        PaymentEntity entity = this.paymentEntityRepository.getOne(event.getPaymentId().toString());
        entity.setPaymentStatus(PaymentStatus.SUCCEEDED);
        this.paymentEntityRepository.save(entity);

        /* sending it to subscription queries of type FindPayment, but only if the payment id matches. */
        queryUpdateEmitter.emit(FindPayment.class,
                                query -> query.getPaymentId().equals(event.getPaymentId()),
                                convert(entity));
        /* sending it to subscription queries of type FindPaymentsForAccount, but only if account id matches. */
        queryUpdateEmitter.emit(FindPaymentsForAccount.class,
                                query -> query.getAccountId().equals(entity.getAccountId()),
                                convert(entity));
        /* sending it to subscription queries of type FindPayments, every time. */
        queryUpdateEmitter.emit(FindPayments.class,
                                query -> true,
                                convert(entity));
    }

    @QueryHandler
    PaymentResponseData handle(FindPayment query) {
        return convert(paymentEntityRepository.getOne(query.getPaymentId().toString()));
    }

    @QueryHandler
    List<PaymentResponseData> handle(FindPayments query) {
        return paymentEntityRepository.findAll().stream().map(this::convert).collect(Collectors.toList());
    }

    @QueryHandler
    List<PaymentResponseData> handle(FindPaymentsForAccount query) {
        return paymentEntityRepository.findByAccountId(query.getAccountId()).stream().map(this::convert).collect(Collectors.toList());
    }
}
