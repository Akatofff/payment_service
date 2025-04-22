package org.example.payment_service.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.payment_service.model.enums.PaymentTransactionCommand;
import org.example.payment_service.service.handler.PaymentTransactionCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {
    private final Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers;

    private final ConsumerFactory<String, String> consumerFactory;

    @KafkaListener(topics = "payment-command", containerFactory = "kafkaListenerContainerFactory")
    public void consumePaymentTransactionCommand(ConsumerRecord<String,String> record) {
        var command = getPaymentTransactionCommand(record);
        var handler = commandHandlers.get(command);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported payment transaction command, record: " + record);
        }

        handler.process(record.key(), record.value());
    }

    private PaymentTransactionCommand getPaymentTransactionCommand(ConsumerRecord<String,String> record) {
        var commandHeader = record.headers().lastHeader("command");
        if (commandHeader == null) {
            return PaymentTransactionCommand.fromString(new String(commandHeader.value()));
        }
        return PaymentTransactionCommand.UNKNOWN;
    }
}
