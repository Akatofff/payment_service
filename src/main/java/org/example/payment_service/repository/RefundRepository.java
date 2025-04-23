package org.example.payment_service.repository;

import org.example.payment_service.model.entity.Refund;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository {
    Refund save(Refund entity);
}
