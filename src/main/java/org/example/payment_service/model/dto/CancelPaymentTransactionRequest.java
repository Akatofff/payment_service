package org.example.payment_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentTransactionRequest {
    @NotNull(message = "Transaction ID must not be null")
    private Long transactionId;
    @NotNull(message = "Cancel amount must not be null")
    @Min(value = 1, message = "Cancel amount must be greater than zero")
    private BigDecimal refundedAmount;
    private String reason;
}
