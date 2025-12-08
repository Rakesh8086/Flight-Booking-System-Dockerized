package com.example.booking.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Passenger validPassenger() {
        Booking booking = new Booking();
        booking.setPnr("PNR123");
        booking.setUserName("AAA");
        booking.setUserEmail("AAA@email.com");
        booking.setMobileNumber("9999999999");
        booking.setBookingDate(java.time.LocalDateTime.now());
        booking.setNumberOfSeats(1);
        booking.setMealOpted("Veg");
        booking.setFlightId(1L);
        booking.setJourneyDate(java.time.LocalDate.now().plusDays(1));

        return new Passenger(
                1L,
                "BBB",
                "CCC",
                30,
                "12A",
                booking
        );
    }

    @Test
    void validPassengerShouldPass() {
        Passenger passenger = validPassenger();
        Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankNameShouldFail() {
        Passenger passenger = validPassenger();
        passenger.setName("");
        Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidAgeShouldFail() {
        Passenger passenger = validPassenger();
        passenger.setAge(-1);
        Set<ConstraintViolation<Passenger>> violations = validator.validate(passenger);
        assertFalse(violations.isEmpty());
    }
}