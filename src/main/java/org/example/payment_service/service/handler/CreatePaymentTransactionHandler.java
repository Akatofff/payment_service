package org.example.payment_service.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.controller.kafka.producer.PaymentTransactionProducer;
import org.example.payment_service.mapper.PaymentTransactionMapper;
import org.example.payment_service.model.dto.CreatePaymentTransactionRequest;
import org.example.payment_service.model.entity.BankAccount;
import org.example.payment_service.model.enums.PaymentTransactionCommand;
import org.example.payment_service.model.enums.PaymentTransactionStatus;
import org.example.payment_service.service.BankAccountService;
import org.example.payment_service.service.PaymentTransactionService;
import org.example.payment_service.service.PaymentTransactionValidator;
import org.example.payment_service.util.JsonConverter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final BankAccountService bankAccountService;


    @Override
    public void processCommand(Long requestId, String message) {
        var request = jsonConverter.fromJson(message, CreatePaymentTransactionRequest.class);
        paymentTransactionValidator.validateCreateTransactionRequest(request);

        var sourceAccount = bankAccountService.findOptionalById(request.getSourceAccountId()).get();
        BankAccount destinationBankAccount = null;
        if (request.getDestinationAccountId() != null) {
            destinationBankAccount = bankAccountService.findOptionalById(request.getDestinationAccountId()).get();
        }

        var result = paymentTransactionService.save(
                paymentTransactionMapper.toEntity(request, sourceAccount, destinationBankAccount, PaymentTransactionStatus.SUCCESS)
        );
        paymentTransactionProducer.sendCommandResult(requestId, PaymentTransactionCommand.CREATE, result.toString());
    }

}
