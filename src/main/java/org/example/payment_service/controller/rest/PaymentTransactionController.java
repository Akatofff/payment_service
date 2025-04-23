package org.example.payment_service.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.service.PaymentTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentTransactionController implements TransactionsApi {
    private final PaymentTransactionService paymentTransactionService;

    @Override
    public ResponseEntity<PaymentTransactionResponse> transactionsTransactionIdGet(Long transactionId) {
        return ResponseEntity.ok(paymentTransactionService.findById(transactionId));
    }
}

