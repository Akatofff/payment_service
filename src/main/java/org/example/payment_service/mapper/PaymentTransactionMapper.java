package org.example.payment_service.mapper;

import org.example.payment_service.model.dto.CreatePaymentTransactionRequest;
import org.example.payment_service.model.dto.CreatePaymentTransactionResponse;
import org.example.payment_service.model.entity.PaymentTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    PaymentTransaction toEntity (CreatePaymentTransactionRequest request);
    CreatePaymentTransactionResponse toResponse (PaymentTransaction paymentTransaction);
}
