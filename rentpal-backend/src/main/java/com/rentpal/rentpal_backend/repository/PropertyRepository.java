package com.rentpal.rentpal_backend.repository;

import com.rentpal.rentpal_backend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwnerOwnerId(Long ownerId);
    List<Property> findByStatus(String status);
}