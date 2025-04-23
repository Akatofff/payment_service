package org.example.payment_service.service.handler;

import jakarta.transaction.Transactional;

public interface PaymentTransactionCommandHandler {
    void processCommand(Long requestId, String message);
}
