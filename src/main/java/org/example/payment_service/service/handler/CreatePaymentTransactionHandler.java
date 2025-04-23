package org.example.payment_service.service.handler;

import jakarta.transaction.Transactional;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final BankAccountService bankAccountService;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionProducer paymentTransactionProducer;

    @Override
    @Transactional
    public void process(String requestId, String message) {
        var request = jsonConverter.toObject(message, CreatePaymentTransactionRequest.class);
        paymentTransactionValidator.validateCreatePaymentTransactionRequest(request);

        var sourceBankAccount = bankAccountService.findById(request.getSourceBankAccountId()).get();
        sourceBankAccount.setBalance(
                sourceBankAccount.getBalance().subtract(request.getAmount())
        );

        Optional<BankAccount> destinationBankAccount = Optional.empty();
        if (request.getDestinationBankAccountId() != null) {
            destinationBankAccount = bankAccountService.findById(request.getDestinationBankAccountId());
            destinationBankAccount.get().setBalance(
                    destinationBankAccount.get().getBalance().add(request.getAmount())
            );
        }

        var entity = paymentTransactionMapper.toEntity(request);
        entity.setSourceBankAccount(sourceBankAccount);
        destinationBankAccount.ifPresent(entity::setDestinationBankAccount);
        entity.setPaymentTransactionStatus(PaymentTransactionStatus.SUCCESS);

        var savedEntity = paymentTransactionService.save(entity);

        if (destinationBankAccount.isPresent()) {
            bankAccountService.saveAll(List.of(sourceBankAccount, destinationBankAccount.get()));
        } else {
            bankAccountService.saveAll(List.of(sourceBankAccount));
        }

        paymentTransactionProducer.sendCommandResult(
                PaymentTransactionProducer.RESULT_TOPIC,
                requestId,
                jsonConverter.toJson(savedEntity),
                PaymentTransactionCommand.CREATE
                );
    }
}
