package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByOwner_OwnerId(Long ownerId);
    List<Complaint> findByTenant_TenantId(Long tenantId);
}
