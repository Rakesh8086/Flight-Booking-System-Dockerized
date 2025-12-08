package com.example.booking.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FlightDTOTest {

    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory f = Validation.buildDefaultValidatorFactory();
        validator = f.getValidator();
    }

    private FlightDTO validFlight() {
        return new FlightDTO(
                1L,
                "AirX",
                "TTT",
                "UUU",
                LocalDate.now().plusDays(1),
                LocalTime.now(),
                LocalTime.now().plusHours(2),
                3000.0,
                100,
                90
        );
    }

    @Test
    void validFlightPasses() {
        FlightDTO dto = validFlight();
        Set<ConstraintViolation<FlightDTO>> v = validator.validate(dto);
        assertTrue(v.isEmpty());
    }

    @Test
    void negativePriceFails() {
        FlightDTO dto = validFlight();
        dto.setPrice(-1.0);
        Set<ConstraintViolation<FlightDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void zeroTotalSeatsFails() {
        FlightDTO dto = validFlight();
        dto.setTotalSeats(0);
        Set<ConstraintViolation<FlightDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void negativeAvailableSeatsFails() {
        FlightDTO dto = validFlight();
        dto.setAvailableSeats(-1);
        Set<ConstraintViolation<FlightDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }
}
