package com.example.booking.entity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Booking validBooking() {
        return new Booking(
                "PNR123",
                "AAA",
                "AAA@example.com",
                "9999999999",
                LocalDateTime.now(),
                2,
                "Veg",
                5000.0,
                1L,
                LocalDate.now().plusDays(5),
                null
        );
    }

    @Test
    void validBookingShouldPass() {
        Booking booking = validBooking();
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmailShouldFail() {
        Booking booking = validBooking();
        booking.setUserEmail("wrong");
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidMobileShouldFail() {
        Booking booking = validBooking();
        booking.setMobileNumber("12");
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
    }

    @Test
    void invalidSeatsShouldFail() {
        Booking booking = validBooking();
        booking.setNumberOfSeats(0);
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        assertFalse(violations.isEmpty());
    }
}