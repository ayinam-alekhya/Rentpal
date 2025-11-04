package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.OwnerSummaryDTO;
import com.rentpal.rentpal_backend.model.Owner;
import com.rentpal.rentpal_backend.repository.OwnerRepository;
import com.rentpal.rentpal_backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    public List<OwnerSummaryDTO> getAllOwners() {
        List<Owner> owners = ownerRepository.findAll();
        return owners.stream()
                .map(o -> new OwnerSummaryDTO(
                        o.getOwnerId(),
                        o.getName(),
                        o.getEmail(),
                        o.getPhone()
                ))
                .toList();
    }


    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + id));
    }

    public Owner createOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    public Owner updateOwner(Long id, Owner updatedOwner) {
        Owner existingOwner = getOwnerById(id);
        existingOwner.setName(updatedOwner.getName());
        existingOwner.setEmail(updatedOwner.getEmail());
        existingOwner.setPhone(updatedOwner.getPhone());
        return ownerRepository.save(existingOwner);
    }

    public void deleteOwner(Long id) {
        Owner existingOwner = getOwnerById(id);
        ownerRepository.delete(existingOwner);
    }
}
