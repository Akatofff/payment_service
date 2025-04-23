package org.example.payment_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.mapper.PaymentTransactionMapper;
import org.example.payment_service.model.dto.CreatePaymentTransactionResponse;
import org.example.payment_service.model.entity.PaymentTransaction;
import org.example.payment_service.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;

    @Transactional
    public CreatePaymentTransactionResponse save(PaymentTransaction paymentTransaction) {
        var entity = paymentTransactionRepository.save(paymentTransaction);
        return paymentTransactionMapper.toKafkaDto(entity);
    }

    @Transactional
    public Optional<PaymentTransaction> findOptionalById(@NotNull Long id){
        try{
            return paymentTransactionRepository.findById(id);
        } catch (EntityNotFoundException e){
            return Optional.empty();
        }
    }

    @Transactional
    public PaymentTransactionResponse findById(@NotNull Long id){
        var entity = paymentTransactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Payment transaction with id " + id + " not found")
        );
        return paymentTransactionMapper.toDto(entity);
    }

}
