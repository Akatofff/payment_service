package org.example.payment_service.service.handler;

public interface PaymentTransactionCommandHandler {
    void process(String requestId, String message);
}
