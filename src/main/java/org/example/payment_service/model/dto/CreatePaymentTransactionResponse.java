package org.example.payment_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.payment_service.model.dto.enums.CommandResultStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionResponse {
    private CommandResultStatus status;
    private String errorMessage;
    private LocalDateTime executedAt;
}
