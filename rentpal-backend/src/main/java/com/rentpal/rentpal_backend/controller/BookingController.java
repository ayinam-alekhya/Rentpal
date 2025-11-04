package com.rentpal.rentpal_backend.controller;

import com.rentpal.rentpal_backend.dto.BookingRequestDTO;
import com.rentpal.rentpal_backend.dto.BookingSummaryDTO;
import com.rentpal.rentpal_backend.model.Booking;
import com.rentpal.rentpal_backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // CREATE - Create a new booking request
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody BookingRequestDTO requestDTO) {
        try {
            Booking booking = bookingService.createBooking(requestDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking request sent successfully");
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create booking: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // READ - Get all bookings for a tenant
    @GetMapping("/tenant/{tenantId}")
    public List<Booking> getBookingsByTenantId(@PathVariable Long tenantId) {
        return bookingService.getBookingsByTenantId(tenantId);
    }

    // READ - Get all bookings for an owner
    @GetMapping("/owner/{ownerId}")
    public List<Booking> getBookingsByOwnerId(@PathVariable Long ownerId) {
        return bookingService.getBookingsByOwnerId(ownerId);
    }

    // READ - Get booking by ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Booking not found: " + e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    // READ - Get all bookings as summary DTOs for a tenant
    @GetMapping("/tenant/{tenantId}/summary")
    public List<BookingSummaryDTO> getBookingSummariesByTenantId(@PathVariable Long tenantId) {
        return bookingService.getBookingSummariesByTenantId(tenantId);
    }

    // READ - Get all bookings as summary DTOs for an owner
    @GetMapping("/owner/{ownerId}/summary")
    public List<BookingSummaryDTO> getBookingSummariesByOwnerId(@PathVariable Long ownerId) {
        return bookingService.getBookingSummariesByOwnerId(ownerId);
    }

    // UPDATE - Approve a booking
    @PutMapping("/{bookingId}/approve")
    public ResponseEntity<Map<String, Object>> approveBooking(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.approveBooking(bookingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking approved successfully");
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to approve booking: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // UPDATE - Reject a booking
    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<Map<String, Object>> rejectBooking(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.rejectBooking(bookingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking rejected successfully");
            response.put("booking", booking);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to reject booking: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // DELETE - Delete a booking
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long bookingId) {
        try {
            bookingService.deleteBooking(bookingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete booking: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}