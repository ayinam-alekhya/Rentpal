package com.rentpal.service;

import com.rentpal.dto.BookingRequestDTO;
import com.rentpal.dto.BookingSummaryDTO;
import com.rentpal.utils.ApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public class BookingService {
    
    public BookingSummaryDTO createBooking(BookingRequestDTO bookingRequest) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInput = mapper.writeValueAsString(bookingRequest);
        String jsonOutput = ApiUtil.post("/bookings", jsonInput);
        return mapper.readValue(jsonOutput, BookingSummaryDTO.class);
    }
    
    public List<BookingSummaryDTO> getBookingsByTenantId(Long tenantId) throws Exception {
        String json = ApiUtil.get("/bookings/tenant/" + tenantId + "/summary");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<BookingSummaryDTO>>(){});
    }
    
    public List<BookingSummaryDTO> getBookingsByOwnerId(Long ownerId) throws Exception {
        String json = ApiUtil.get("/bookings/owner/" + ownerId + "/summary");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<BookingSummaryDTO>>(){});
    }
    
    public BookingSummaryDTO approveBooking(Long bookingId) throws Exception {
        String jsonOutput = ApiUtil.put("/bookings/" + bookingId + "/approve", "");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonOutput, BookingSummaryDTO.class);
    }
    
    public BookingSummaryDTO rejectBooking(Long bookingId) throws Exception {
        String jsonOutput = ApiUtil.put("/bookings/" + bookingId + "/reject", "");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonOutput, BookingSummaryDTO.class);
    }
    
    public boolean deleteBooking(Long bookingId) throws Exception {
        return ApiUtil.delete("/bookings/" + bookingId);
    }
}