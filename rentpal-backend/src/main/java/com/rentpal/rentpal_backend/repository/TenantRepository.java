package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByEmailIgnoreCase(String email);
    List<Tenant> findByName(String name);
    List<Tenant> findAllByEmailIgnoreCase(String email);
    List<Tenant> findByOwner_OwnerId(Long ownerId);
    // If you decide to enforce per-owner uniqueness:
    Optional<Tenant> findByEmailIgnoreCaseAndOwner_OwnerId(String email, Long ownerId);

}