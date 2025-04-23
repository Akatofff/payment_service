package org.example.payment_service.controller.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.payment_service.model.enums.PaymentTransactionCommand;
import org.example.payment_service.service.handler.PaymentTransactionCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {
    private final Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers;

    @KafkaListener(topics = "payment-command", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommand(ConsumerRecord<String, String> record) throws JsonProcessingException {
        log.info("Payment command received, command:{}", record);

        if (extractCommand(record).equals(PaymentTransactionCommand.UNKNOWN)) {
            throw new IllegalArgumentException("Unknown command");
        }

        commandHandlers.get(extractCommand(record)).processCommand(Long.valueOf(record.key()), record.value());
    }

    private PaymentTransactionCommand extractCommand(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader("command");
        if (header != null) {
            return PaymentTransactionCommand.fromString(new String(header.value(), StandardCharsets.UTF_8));
        }
        return PaymentTransactionCommand.UNKNOWN;
    }

}
