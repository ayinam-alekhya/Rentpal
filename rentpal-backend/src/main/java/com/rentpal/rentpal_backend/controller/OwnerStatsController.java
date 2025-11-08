package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.OwnerStatsDTO;
import com.rentpal.rentpal_backend.service.OwnerStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners/{ownerId}/stats")
@CrossOrigin(origins = "*")
public class OwnerStatsController {

    @Autowired
    private OwnerStatsService statsService;

    @GetMapping
    public OwnerStatsDTO getStats(@PathVariable Long ownerId) {
        return statsService.getStats(ownerId);
    }
}
