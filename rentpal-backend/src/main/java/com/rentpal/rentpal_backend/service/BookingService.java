package com.rentpal.rentpal_backend.service;

import com.rentpal.rentpal_backend.dto.BookingRequestDTO;
import com.rentpal.rentpal_backend.dto.BookingSummaryDTO;
import com.rentpal.rentpal_backend.model.Booking;
import com.rentpal.rentpal_backend.model.Property;
import com.rentpal.rentpal_backend.model.Tenant;
import com.rentpal.rentpal_backend.repository.BookingRepository;
import com.rentpal.rentpal_backend.repository.PropertyRepository;
import com.rentpal.rentpal_backend.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    // CREATE - Create a new booking request
    public Booking createBooking(BookingRequestDTO requestDTO) {
        Tenant tenant = tenantRepository.findById(requestDTO.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + requestDTO.getTenantId()));
        
        Property property = propertyRepository.findById(requestDTO.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found with ID: " + requestDTO.getPropertyId()));

        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setProperty(property);
        booking.setMessage(requestDTO.getMessage());
        booking.setStatus("Pending");

        return bookingRepository.save(booking);
    }

    // READ - Get all bookings for a tenant
    public List<Booking> getBookingsByTenantId(Long tenantId) {
        return bookingRepository.findByTenantTenantId(tenantId);
    }

    // READ - Get all bookings for an owner
    public List<Booking> getBookingsByOwnerId(Long ownerId) {
        return bookingRepository.findByPropertyOwnerOwnerId(ownerId);
    }

    // READ - Get booking by ID
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
    }

    // READ - Get all bookings as summary DTOs for a tenant
    public List<BookingSummaryDTO> getBookingSummariesByTenantId(Long tenantId) {
        List<Booking> bookings = bookingRepository.findByTenantTenantId(tenantId);
        return bookings.stream()
                .map(b -> new BookingSummaryDTO(
                        b.getBookingId(),
                        b.getStatus(),
                        b.getRequestDate(),
                        b.getResponseDate(),
                        b.getMessage(),
                        b.getTenant() != null ? b.getTenant().getTenantId() : null,
                        b.getTenant() != null ? b.getTenant().getName() : null,
                        b.getProperty() != null ? b.getProperty().getPropertyId() : null,
                        b.getProperty() != null ? b.getProperty().getTitle() : null,
                        b.getProperty() != null ? b.getProperty().getRentAmount() : 0.0,
                        b.getProperty() != null ? b.getProperty().getLocation() : null
                ))
                .collect(Collectors.toList());
    }

    // READ - Get all bookings as summary DTOs for an owner
    public List<BookingSummaryDTO> getBookingSummariesByOwnerId(Long ownerId) {
        List<Booking> bookings = bookingRepository.findByPropertyOwnerOwnerId(ownerId);
        return bookings.stream()
                .map(b -> new BookingSummaryDTO(
                        b.getBookingId(),
                        b.getStatus(),
                        b.getRequestDate(),
                        b.getResponseDate(),
                        b.getMessage(),
                        b.getTenant() != null ? b.getTenant().getTenantId() : null,
                        b.getTenant() != null ? b.getTenant().getName() : null,
                        b.getProperty() != null ? b.getProperty().getPropertyId() : null,
                        b.getProperty() != null ? b.getProperty().getTitle() : null,
                        b.getProperty() != null ? b.getProperty().getRentAmount() : 0.0,
                        b.getProperty() != null ? b.getProperty().getLocation() : null
                ))
                .collect(Collectors.toList());
    }

    // UPDATE - Approve a booking
    public Booking approveBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.approve();
        return bookingRepository.save(booking);
    }

    // UPDATE - Reject a booking
    public Booking rejectBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        booking.reject();
        return bookingRepository.save(booking);
    }

    // DELETE - Delete a booking
    public void deleteBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        bookingRepository.delete(booking);
    }
}