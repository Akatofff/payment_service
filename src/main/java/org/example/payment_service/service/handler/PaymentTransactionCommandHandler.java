package org.example.payment_service.service.handler;

public interface PaymentTransactionCommandHandler {
    void processCommand(Long requestId, String message);
}
