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
    @NotNull
    @Min(value = 1, message = "Refunded amount must not be null")
    private BigDecimal refundedAmount;
    private String reason;
}
