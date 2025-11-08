package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.OwnerStatsDTO;
import com.rentpal.rentpal_backend.repository.TenantRepository;
import com.rentpal.rentpal_backend.repository.PaymentRepository;
import com.rentpal.rentpal_backend.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnerStatsService {

    @Autowired
    private TenantRepository tenantRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired(required = false)
    private ComplaintRepository complaintRepo; // optional, only if you have complaints

    @Transactional(readOnly = true)
    public OwnerStatsDTO getStats(Long ownerId) {
        OwnerStatsDTO dto = new OwnerStatsDTO();
        dto.setTenantCount(tenantRepo.countByOwner_OwnerId(ownerId));
        dto.setPaymentCount(paymentRepo.countByTenant_Owner_OwnerId(ownerId));

        dto.setTotalCollected(paymentRepo.sumCollectedByOwner(ownerId));
        dto.setTotalPending(paymentRepo.sumPendingByOwner(ownerId));

        if (complaintRepo != null) {
            dto.setComplaintCount(complaintRepo.countByOwner_OwnerId(ownerId));
        } else {
            dto.setComplaintCount(0);
        }

        return dto;
    }
}
