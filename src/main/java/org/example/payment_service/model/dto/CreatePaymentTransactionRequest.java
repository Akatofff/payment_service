package org.example.payment_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionRequest {
    @NotNull
    private Long sourceBankAccountId;
    private Long destinationBankAccountId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;
    private String description;

    public @NotNull Long getSourceAccountId() {
        return sourceBankAccountId;
    }

    public @NotNull Long getDestinationAccountId() {
        return destinationBankAccountId;
    }
}
