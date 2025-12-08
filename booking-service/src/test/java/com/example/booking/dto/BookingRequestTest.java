package com.example.booking.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestTest {

    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory f = Validation.buildDefaultValidatorFactory();
        validator = f.getValidator();
    }

    private PassengerDTO samplePassenger() {
        return new PassengerDTO("BBB", "Male", 22, "12A");
    }

    private BookingRequest validRequest() {
        return new BookingRequest(
                1L,
                "AAA",
                "AAA@example.com",
                LocalDate.now().plusDays(1),
                "9999999999",
                "Veg",
                List.of(samplePassenger())
        );
    }

    @Test
    void validRequestPasses() {
        BookingRequest br = validRequest();
        Set<ConstraintViolation<BookingRequest>> v = validator.validate(br);
        assertTrue(v.isEmpty());
    }

    @Test
    void invalidEmailFails() {
        BookingRequest br = validRequest();
        br.setUserEmail("bad");
        Set<ConstraintViolation<BookingRequest>> v = validator.validate(br);
        assertFalse(v.isEmpty());
    }

    @Test
    void invalidMealOptionFails() {
        BookingRequest br = validRequest();
        br.setMealOpted("ABC");
        Set<ConstraintViolation<BookingRequest>> v = validator.validate(br);
        assertFalse(v.isEmpty());
    }

    @Test
    void emptyPassengerListFails() {
        BookingRequest br = validRequest();
        br.setPassengers(List.of());
        Set<ConstraintViolation<BookingRequest>> v = validator.validate(br);
        assertFalse(v.isEmpty());
    }
}
