package com.example.booking.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PassengerDTOTest {

    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory f = Validation.buildDefaultValidatorFactory();
        validator = f.getValidator();
    }

    private PassengerDTO validPassenger() {
        return new PassengerDTO("AAA", "Male", 25, "12A");
    }

    @Test
    void validPassengerPasses() {
        PassengerDTO dto = validPassenger();
        Set<ConstraintViolation<PassengerDTO>> v = validator.validate(dto);
        assertTrue(v.isEmpty());
    }

    @Test
    void blankNameFails() {
        PassengerDTO dto = validPassenger();
        dto.setName("");
        Set<ConstraintViolation<PassengerDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }

    @Test
    void negativeAgeFails() {
        PassengerDTO dto = validPassenger();
        dto.setAge(-5);
        Set<ConstraintViolation<PassengerDTO>> v = validator.validate(dto);
        assertFalse(v.isEmpty());
    }
}