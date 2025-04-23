package org.example.payment_service.controller.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.model.enums.PaymentTransactionCommand;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionProducer {
    public final static String RESULT_TOPIC = "payment-command-result";
    public final static String COMMAND_TOPIC = "payment-command";

    private final static String PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER = "command";
    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendCommandResult(String topic,
                                  String requestId,
                                  String message,
                                  PaymentTransactionCommand command) {
        var kafkaMessage = buildMessage(topic, requestId, message, command);
        kafkaTemplate.send(
                kafkaMessage
        );
        log.info("Successfully send command result: {}", kafkaMessage);
    }

    private Message<String> buildMessage(String topic,
                                 String requestId,
                                 String message,
                                 PaymentTransactionCommand command) {
        return MessageBuilder.withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, requestId)
                .setHeader(PAYMENT_TRANSACTION_COMMAND_TYPE_HEADER, command.toString())
                .build();
    }
}
