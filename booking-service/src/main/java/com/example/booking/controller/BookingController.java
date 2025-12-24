package com.example.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.dto.BookingRequest;
import com.example.booking.entity.Booking;
import com.example.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
	@Autowired
	BookingService bookingService;
	
	@PostMapping("/ticket/{flightId}")
    public ResponseEntity<String> bookTicket(
            @PathVariable Long flightId, 
            @Valid @RequestBody BookingRequest request) {
        return bookingService.bookTicket(flightId, request);
	}
	
	@GetMapping("/ticket/{pnr}")
    public ResponseEntity<?> getTicketByPnr(@PathVariable String pnr,
    		@RequestHeader("X-Authenticated-User") String loggedInEmail) {
        Booking booking = bookingService.getTicketByPnr(pnr);
        if(booking != null && booking.getUserEmail().equals(loggedInEmail)) {
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access Denied: You do not own this ticket.");
    }
	
	@GetMapping("/booking/history")
    public ResponseEntity<List<Booking>> getBookingHistoryByEmail( 
    		@RequestHeader("X-Authenticated-User") String loggedInEmail){
		System.out.println("******************Received email in Booking Service***************: " + loggedInEmail);
		List<Booking> history = bookingService.getBookingHistoryByEmail(loggedInEmail);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
	
	@DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<String> cancelTicket(@PathVariable String pnr) {
        return bookingService.cancelTicket(pnr);
    }
}
