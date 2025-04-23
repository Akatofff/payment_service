package org.example.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.mapper.RefundMapper;
import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.dto.CancelPaymentTransactionResponse;
import org.example.payment_service.model.entity.PaymentTransaction;
import org.example.payment_service.model.enums.RefundStatus;
import org.example.payment_service.repository.RefundRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {
    private final RefundMapper refundMapper;
    private final RefundRepository refundRepository;

    public CancelPaymentTransactionResponse createRefund(CancelPaymentTransactionRequest request,
                                                         PaymentTransaction paymentTransaction) {


        var entity = refundMapper.toEntity(request);
        entity.setPaymentTransaction(paymentTransaction);
        entity.setStatus(RefundStatus.COMPLETED);

        var savedEntity = refundRepository.save(entity);

        savedEntity.setPaymentTransaction(paymentTransaction);
        return refundMapper.toResponse(savedEntity);
    }
}
