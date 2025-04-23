package org.example.payment_service.model.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PaymentTransactionCommand {
    CREATE,
    REFUND,
    UNKNOWN;

    public static PaymentTransactionCommand fromString(String command) {
        try {
            return valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
