package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // basic
    List<Payment> findByTenant_TenantId(Long tenantId);

    // by status
    List<Payment> findByTenant_TenantIdAndStatus(Long tenantId, String status);

    // by date range (inclusive)
    List<Payment> findByTenant_TenantIdAndPaymentDateBetween(Long tenantId,
                                                             LocalDateTime from,
                                                             LocalDateTime to);

    // by status + date range
    List<Payment> findByTenant_TenantIdAndStatusAndPaymentDateBetween(Long tenantId,
                                                                      String status,
                                                                      LocalDateTime from,
                                                                      LocalDateTime to);
}
