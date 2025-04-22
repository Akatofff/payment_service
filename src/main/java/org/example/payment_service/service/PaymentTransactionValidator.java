package org.example.payment_service.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.errors.PaymentTransactionValidationException;
import org.example.payment_service.model.dto.CreatePaymentTransactionRequest;
import org.example.payment_service.model.entity.BankAccount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final Validator validator;
    private final BankAccountService bankAccountService;

    public void validateCreatePaymentTransactionRequest(CreatePaymentTransactionRequest request) {

        var violations = validator.validate(request);
        List<String> errors = new ArrayList<>(
                violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()
        );
        Optional<BankAccount> sourceBankAccount = Optional.empty();

        if (request.getSourceBankAccountId() != null) {
            sourceBankAccount = bankAccountService.findById(request.getSourceBankAccountId());
            if (sourceBankAccount.isEmpty()) {
                errors.add("Source bank account not found, source account id: " + request.getSourceBankAccountId());
            }
        }

        if (request.getDestinationBankAccountId() != null) {
            var destinationBankAccount = bankAccountService.findById(request.getDestinationBankAccountId());
            if (destinationBankAccount.isEmpty()) {
                errors.add("Destination bank account not found, destination account id: " + request.getDestinationBankAccountId());
            }
        }

        if (request.getAmount() != null && sourceBankAccount.isPresent()) {
            if (sourceBankAccount.get().getBalance().compareTo(request.getAmount()) < 0) {
                errors.add("Source bank account balance less then requested amount, source account id: " + request.getSourceBankAccountId());
            }
        }

        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }
}
