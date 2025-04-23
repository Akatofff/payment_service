package org.example.payment_service.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.errors.PaymentTransactionValidationException;
import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.dto.CreatePaymentTransactionRequest;
import org.example.payment_service.model.entity.PaymentTransaction;
import org.example.payment_service.model.enums.PaymentTransactionStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final BankAccountService bankAccountService;
    private final PaymentTransactionService paymentTransactionService;
    private final RefundService refundService;
    private final Validator validator;

    public void validateCreateTransactionRequest(CreatePaymentTransactionRequest transaction) {
        List<String> errors = new ArrayList<>();

        var violations = validator.validate(transaction, CreatePaymentTransactionRequest.class);
        var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
        errors.addAll(violationMessages);

        if (bankAccountService.findOptionalById(transaction.getSourceAccountId()).isEmpty()) {
            errors.add("Source bank account does not exist: " + transaction.getSourceAccountId());
        }

        if (transaction.getDestinationAccountId() != null &&
                bankAccountService.findOptionalById(transaction.getDestinationAccountId()).isEmpty()) {
            errors.add("Destination bank account does not exist: " + transaction.getDestinationAccountId());
        }

        if(!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }

    public void validateCancelTransactionRequest(CancelPaymentTransactionRequest cancelPaymentRequest){
        List<String> errors = new ArrayList<>();

        var violations = validator.validate(cancelPaymentRequest, CancelPaymentTransactionRequest.class);
        var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
        errors.addAll(violationMessages);

        PaymentTransaction sourceTransaction = paymentTransactionService.findOptionalById(cancelPaymentRequest.getTransactionId())
                .orElse(null);
        if (sourceTransaction == null) {
            errors.add("Transaction with ID " + cancelPaymentRequest.getTransactionId() + " does not exist.");
        } else {
            // Проверяем, завершена ли транзакция (FAILED или SUCCESS)
            if (!sourceTransaction.getStatus().equals(PaymentTransactionStatus.SUCCESS)) {
                errors.add("Transaction is not completed successfully, cancellation is not allowed.");
            }

            // Вычисляем уже возвращенную сумму.
            var alreadyRefunded = refundService.getTotalRefundedAmount(sourceTransaction.getId());
            var remainingAmount = cancelPaymentRequest.getRefundedAmount().subtract(alreadyRefunded);

            if (cancelPaymentRequest.getRefundedAmount().compareTo(remainingAmount) > 0) {
                errors.add("Cancel amount exceeds available refundable balance. Available: " + remainingAmount);
            }
        }

        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }

}
