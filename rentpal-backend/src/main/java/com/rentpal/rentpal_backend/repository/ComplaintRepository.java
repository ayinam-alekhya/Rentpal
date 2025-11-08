package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    long countByOwner_OwnerId(Long ownerId);
    List<Complaint> findByOwner_OwnerId(Long ownerId);
    List<Complaint> findByOwner_OwnerIdAndStatusIgnoreCase(Long ownerId, String status);
    List<Complaint> findByTenant_TenantId(Long tenantId);
    List<Complaint> findByTenant_TenantIdAndStatusIgnoreCase(Long tenantId, String status);
}
