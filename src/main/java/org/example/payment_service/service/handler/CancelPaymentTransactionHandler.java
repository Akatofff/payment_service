package org.example.payment_service.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.controller.kafka.producer.PaymentTransactionProducer;
import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.enums.PaymentTransactionCommand;
import org.example.payment_service.service.BankAccountService;
import org.example.payment_service.service.PaymentTransactionService;
import org.example.payment_service.service.PaymentTransactionValidator;
import org.example.payment_service.service.RefundService;
import org.example.payment_service.util.JsonConverter;
import org.springframework.stereotype.Service;

import javax.swing.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final PaymentTransactionService paymentTransactionService;
    private final BankAccountService bankAccountService;
    private final RefundService refundService;
    private final PaymentTransactionProducer paymentTransactionProducer;

    @Override
    public void process(String requestId, String message) {
        var request = jsonConverter.toObject(message, CancelPaymentTransactionRequest.class);
        paymentTransactionValidator.validateCancelPaymentTransactionRequest(request);

        var sourceTransaction = paymentTransactionService.findById(request.getTransactionId()).get();
        var sourceBankAccount = sourceTransaction.getSourceBankAccount();

        sourceBankAccount.setBalance(
                sourceBankAccount.getBalance().add(request.getRefundedAmount())
        );

        if (sourceTransaction.getDestinationBankAccount() != null) {
            var destinationBankAccount = sourceTransaction.getDestinationBankAccount();
            destinationBankAccount.setBalance(
                    destinationBankAccount.getBalance().subtract(request.getRefundedAmount())
            );
        }
        var response = refundService.createRefund(request, sourceTransaction);

        paymentTransactionProducer.sendCommandResult(
                PaymentTransactionProducer.RESULT_TOPIC,
                requestId,
                jsonConverter.toJson(response),
                PaymentTransactionCommand.REFUND
        );

    }
}
