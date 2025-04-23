package org.example.payment_service.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.errors.PaymentTransactionValidationException;
import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.dto.CreatePaymentTransactionRequest;
import org.example.payment_service.model.entity.BankAccount;
import org.example.payment_service.model.entity.Refund;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final Validator validator;
    private final BankAccountService bankAccountService;
    private final PaymentTransactionService paymentTransactionService;

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

    public void validateCancelPaymentTransactionRequest(CancelPaymentTransactionRequest request) {
        List<String> errors = new ArrayList<>(validator.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .toList()
        );

        if (request.getTransactionId() != null) {
            var sourceTransaction = paymentTransactionService.findById(request.getTransactionId());
            if (sourceTransaction.isEmpty()) {
                errors.add("Source transaction not found, transaction id: " + request.getTransactionId());
            } else {
                var existedSourceTransaction = sourceTransaction.get();
                var refundedAmount = existedSourceTransaction.getRefunds().stream()
                        .map(Refund::getRefundedAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (existedSourceTransaction.getAmount().subtract(refundedAmount).compareTo(request.getRefundedAmount()) <  0) {
                    errors.add("Requested amount for refund is bigger than source transaction amount, source transaction id: "
                            + request.getTransactionId());
                }
            }

        }

        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }
}
