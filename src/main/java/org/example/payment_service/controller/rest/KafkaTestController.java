package org.example.payment_service.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.example.payment_service.controller.kafka.producer.PaymentTransactionProducer;
import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.dto.CreatePaymentTransactionRequest;
import org.example.payment_service.model.enums.PaymentTransactionCommand;
import org.example.payment_service.util.JsonConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class KafkaTestController {
    private final PaymentTransactionProducer producer;
    private final JsonConverter jsonConverter;

    @PostMapping("/create-payment")
    public String createPaymentTest() {
        var requestId = "request-id";

        CreatePaymentTransactionRequest request = new CreatePaymentTransactionRequest(
                1L,
                2L,
                new BigDecimal("200.00"),
                "USD",
                "Test Payment"
        );

        producer.sendCommandResult(
                request.getSourceAccountId(),
                PaymentTransactionCommand.CREATE,
                jsonConverter.toJson(request)
        );

        return "Create payment command sent with requestId: " + requestId;
    }

    @PostMapping("/cancel-payment")
    public String cancelPaymentTest() {
        var requestId = "request-id";

        CancelPaymentTransactionRequest request = new CancelPaymentTransactionRequest(
                1L,
                new BigDecimal("150.00"),
                "Test Refund"
        );

        producer.sendCommandResult(
                request.getTransactionId(),
                PaymentTransactionCommand.CREATE,
                jsonConverter.toJson(request)
        );

        return "Cancel payment command sent with requestId: " + requestId;
    }

}
