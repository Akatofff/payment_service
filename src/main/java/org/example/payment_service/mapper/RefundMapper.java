package org.example.payment_service.mapper;

import org.example.payment_service.model.dto.CancelPaymentTransactionRequest;
import org.example.payment_service.model.dto.CancelPaymentTransactionResponse;
import org.example.payment_service.model.entity.Refund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefundMapper {
    Refund toEntity(CancelPaymentTransactionRequest request);
    CancelPaymentTransactionResponse toResponse(CancelPaymentTransactionResponse response);
}
