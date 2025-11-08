package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // basic
    List<Payment> findByTenant_TenantId(Long tenantId);
    long countByTenant_Owner_OwnerId(Long ownerId);

    // by status
    List<Payment> findByTenant_TenantIdAndStatus(Long tenantId, String status);
    
    List<Payment> findByTenant_Owner_OwnerId(Long ownerId);

    @Query("""
           select coalesce(sum(p.amount), 0)
           from Payment p
           where p.tenant.owner.ownerId = :ownerId
             and p.status = 'PAID'
           """)
    BigDecimal sumCollectedByOwner(@Param("ownerId") Long ownerId);

    // Sum amounts pending for an owner
    @Query("""
           select coalesce(sum(p.amount), 0)
           from Payment p
           where p.tenant.owner.ownerId = :ownerId
             and p.status = 'PENDING'
           """)
    BigDecimal sumPendingByOwner(@Param("ownerId") Long ownerId);

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
