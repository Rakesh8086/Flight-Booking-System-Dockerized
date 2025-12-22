package com.example.booking.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.FlightDTO;
import com.example.booking.dto.PassengerDTO;
import com.example.booking.entity.Booking;
import com.example.booking.entity.Passenger;
import com.example.booking.exception.BookingNotFoundException;
import com.example.booking.exception.CancellationNotPossibleException;
import com.example.booking.exception.FlightUnavailableException;
import com.example.booking.feign.BookingInterface;
import com.example.booking.notification.BookingMessageSender;
import com.example.booking.repository.BookingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class BookingService {
	private static final Logger log = LoggerFactory.getLogger(BookingService.class);
	
	@Autowired
	BookingRepository bookingRepository;
	@Autowired
	BookingInterface bookingInterface;
	// private final BookingMessageSender messageSender;
    // private final ObjectMapper objectMapper;

	public BookingService(BookingRepository bookingRepository,
			BookingInterface bookingInterface, BookingMessageSender messageSender,
			ObjectMapper objectMapper) {
		this.bookingRepository = bookingRepository;
		this.bookingInterface = bookingInterface;
		// this.messageSender = messageSender;
        // this.objectMapper = objectMapper;
	}

	@Transactional
	@CircuitBreaker(name = "flightServiceBreaker", fallbackMethod = "flightServiceFallback")
	public ResponseEntity<String> bookTicket(Long flightId, BookingRequest bookingRequest) {
		FlightDTO flightDto;
		try {
			flightDto = bookingInterface.getFlightById(flightId);
		} 
		catch (FeignException e) {
			return new ResponseEntity<>("Ticket Booking is currently unavailable due to Flight Service failure.",
					HttpStatus.SERVICE_UNAVAILABLE);

		}

		int seatsToBook = bookingRequest.getPassengers().size();
		if (seatsToBook <= 0) {
			throw new FlightUnavailableException("Number of seats must be at least one.");
		}
		Integer currentAvailableSeats = flightDto.getAvailableSeats();
		if (currentAvailableSeats < seatsToBook) {
			throw new FlightUnavailableException(
					"Insufficient seats available. Requested: " + seatsToBook +
							", Available: " + currentAvailableSeats);
		}
		flightDto.setAvailableSeats(currentAvailableSeats - seatsToBook);
		bookingInterface.updateFlightInventory(flightDto);
		
		Booking booking = requestToEntity(bookingRequest, flightDto, flightId);
		bookingRepository.save(booking);
		
		/*try{
            String email = bookingRequest.getUserEmail(); // Assuming email is in the request
            String pnr = booking.getPnr();
            String flightDetails = flightDto.getAirlineName() + " (" + flightDto.getFromPlace() + " to " + flightDto.getToPlace() + ")";
            
            String notificationPayload = objectMapper.writeValueAsString(
                Map.of("pnr", pnr, "email", email, "flightDetails", 
                		flightDetails, "message", "Booking confirmed.")
            );         
            messageSender.sendBookingConfirmation(notificationPayload);
        } 
		catch(Exception e) {
            // we dont throw exception if it fails because booking is successful
			// only email is not sent
            log.error("Failed to send RabbitMQ notification for PNR: {}", 
            		booking.getPnr(), e);
        }*/

		return new ResponseEntity<>(booking.getPnr(), HttpStatus.CREATED);
	}

	private Passenger mapPassengerDtoToEntity(PassengerDTO dto) {
		Passenger passenger = new Passenger();
		passenger.setName(dto.getName());
		passenger.setGender(dto.getGender());
		passenger.setAge(dto.getAge());
		passenger.setSeatNumber(dto.getSeatNumber());

		return passenger;
	}

	private Booking requestToEntity(BookingRequest bookingRequest,
			FlightDTO flightDto, Long flightId) {

		Booking booking = new Booking();
		booking.setPnr(generateUniquePNR());
		booking.setUserEmail(bookingRequest.getUserEmail());
		booking.setUserName(bookingRequest.getUserName());
		booking.setNumberOfSeats(bookingRequest.getPassengers().size());
		booking.setBookingDate(LocalDateTime.now());
		booking.setJourneyDate(flightDto.getScheduleDate());
		booking.setFlightId(flightId);
		booking.setMealOpted(bookingRequest.getMealOpted());
		booking.setMobileNumber(bookingRequest.getMobileNumber());
		booking.setTotalCost(bookingRequest.getPassengers().size() * flightDto.getPrice());

		List<Passenger> passengers = bookingRequest.getPassengers().stream()
				.map(this::mapPassengerDtoToEntity)
				.collect(Collectors.toList());
		for (Passenger passenger : passengers) {
			passenger.setBooking(booking);
		}
		booking.setPassengers(passengers);

		return booking;
	}

	private String generateUniquePNR() {
		return "CHUBBFLIGHT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	}
	
	public Booking getTicketByPnr(String pnr) {
		Optional<Booking> bookingOptional = bookingRepository.findByPnr(pnr);
		if (!bookingOptional.isPresent()) {
			throw new BookingNotFoundException(
					"Ticket with PNR " + pnr + " not found.");
		}

		return bookingOptional.get();
	}
	
	public List<Booking> getBookingHistoryByEmail(String emailId) {
		List<Booking> history = bookingRepository.findByUserEmailOrderByBookingDateDesc(emailId);
		if (history.isEmpty()) {
			throw new BookingNotFoundException(
					"No booking history found for email: " + emailId);
		}

		return history;
	}
	
	@Transactional
	@CircuitBreaker(name = "flightServiceBreaker", fallbackMethod = "flightServiceFallback")
	public ResponseEntity<String> cancelTicket(String pnr) {
		Booking booking = getTicketByPnr(pnr);
		Long flightId = booking.getFlightId();
		int cancelledSeats = booking.getNumberOfSeats();
		FlightDTO flightDto;
		try {
			flightDto = bookingInterface.getFlightById(flightId);
		} 
		catch (FeignException e) {
			return new ResponseEntity<>("Ticket Cancelling is currently unavailable due to Flight Service failure.",
					HttpStatus.SERVICE_UNAVAILABLE);

		}

		// Check if the current time is AFTER the deadline
		LocalDateTime departureTime = booking.getJourneyDate().atTime(flightDto.getDepartureTime());
		LocalDateTime cancellationDeadline = departureTime.minus(
				24, ChronoUnit.HOURS);

		if (LocalDateTime.now().isAfter(cancellationDeadline)) {
			throw new CancellationNotPossibleException(
					"Cancellation failed. Tickets must be cancelled at least "
							+ "24 hours prior to departure time");
		}
		flightDto.setAvailableSeats(flightDto.getAvailableSeats() + cancelledSeats);
		bookingInterface.updateFlightInventory(flightDto);
		bookingRepository.delete(booking);
		
		return new ResponseEntity<>("Ticket with PNR " + pnr + 
        		" cancelled successfully.", HttpStatus.OK);
	}
	
	public ResponseEntity<String> flightServiceFallback() {

		return new ResponseEntity<>("Ticket Booking/Cancelling is currently unavailable due to Flight Service failure.",
				HttpStatus.SERVICE_UNAVAILABLE);
	}
}
