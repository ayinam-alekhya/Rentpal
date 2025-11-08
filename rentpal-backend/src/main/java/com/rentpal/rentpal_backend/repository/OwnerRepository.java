package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsByEmail(String email);
    Owner findByEmail(String email);
    List<Owner> findByName(String name);
}