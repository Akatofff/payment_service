package org.example.payment_service.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.controller.kafka.producer.PaymentTransactionProducer;
import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.enums.PaymentTransactionCommand;
import org.example.payment_service.service.PaymentTransactionValidator;
import org.example.payment_service.service.RefundService;
import org.example.payment_service.util.JsonConverter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final RefundService refundService;
    private final PaymentTransactionProducer paymentTransactionProducer;

    @Override
    public void processCommand(Long requestId, String message) {
        var request = jsonConverter.toJson(CancelPaymentTransactionRequest.class);
        paymentTransactionValidator.validateCancelTransactionRequest(request);
        var result = refundService.cancelPayment(request);

        paymentTransactionProducer.sendCommandResult(requestId, PaymentTransactionCommand.REFUND, result.toString());
    }

}
