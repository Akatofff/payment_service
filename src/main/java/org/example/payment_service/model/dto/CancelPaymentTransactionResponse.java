package org.example.payment_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.payment_service.model.dto.enums.CommandResultStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentTransactionResponse {
    private CommandResultStatus status;
    private String errorMessage;
}
