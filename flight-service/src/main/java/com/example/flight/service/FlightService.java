package com.example.flight.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.flight.dto.FlightDTO;
import com.example.flight.entity.Flight;

public interface FlightService {
    Long addFlight(FlightDTO flight);
    List<Flight> searchFlights(String fromPlace, String toPlace, LocalDate scheduleDate);
    Optional<FlightDTO> getFlightById(Long flightId);
    String updateFlightInventory(FlightDTO flightDto);
}