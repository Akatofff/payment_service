package org.example.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.mapper.RefundMapper;
import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.dto.CancelPaymentTransactionResponse;
import org.example.payment_service.model.entity.Refund;
import org.example.payment_service.model.enums.RefundStatus;
import org.example.payment_service.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundRepository refundRepository;
    private final PaymentTransactionService paymentTransactionService;
    private final RefundMapper refundMapper;

    public BigDecimal getTotalRefundedAmount(Long transactionId) {
        var refunds = refundRepository.findAllByPaymentTransactionId(transactionId);

        return refunds.stream()
                .map(Refund::getRefundedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public CancelPaymentTransactionResponse cancelPayment(CancelPaymentTransactionRequest cancelPaymentRequest) {
        var sourceTransaction = paymentTransactionService.findOptionalById(cancelPaymentRequest.getTransactionId()).get();

        sourceTransaction.setAmount(
                sourceTransaction.getAmount().subtract(cancelPaymentRequest.getRefundedAmount())
        );

        var entity = refundRepository.save(
                refundMapper.toEntity(cancelPaymentRequest, RefundStatus.COMPLETED)
        );
        entity.setPaymentTransaction(sourceTransaction);

        return refundMapper.toResponse(entity);
    }

}
