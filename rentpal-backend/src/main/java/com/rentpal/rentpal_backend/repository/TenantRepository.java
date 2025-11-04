package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findByEmail(String email);
    List<Tenant> findByName(String name);
}