package com.example.flight.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.flight.dto.FlightDTO;
import com.example.flight.entity.Flight;
import com.example.flight.service.FlightService;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
@AutoConfigureMockMvc(addFilters = false)
class FlightControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    FlightService flightService;

    @Test
    void addFlightInventoryReturnsCreated() throws Exception {
        Mockito.when(flightService.addFlight(any())).thenReturn(10L);
        String body = """
                {
                  "id": 10,
                  "airlineName": "Air India",
                  "fromPlace": "AAA",
                  "toPlace": "BBB",
                  "scheduleDate": "2026-01-01",
                  "departureTime": "10:00",
                  "arrivalTime": "12:00",
                  "price": 3000,
                  "totalSeats": 100,
                  "availableSeats": 90
                }
                """;

        mvc.perform(post("/api/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

    }

    @Test
    void searchFlightsReturnsList() throws Exception {
        Flight f = new Flight();
        f.setId(1L);
        f.setAirlineName("Air India");
        f.setFromPlace("AAA");
        f.setToPlace("BBB");
        f.setScheduleDate(LocalDate.now().plusDays(1));
        f.setDepartureTime(LocalTime.parse("10:00"));
        f.setArrivalTime(LocalTime.parse("12:00"));
        f.setPrice(3000.0);
        f.setTotalSeats(100);
        f.setAvailableSeats(90);

        Mockito.when(flightService.searchFlights("A", "B", LocalDate.parse("2026-01-01")))
                .thenReturn(List.of(f));
        String body = """
                {
                  "fromPlace": "A",
                  "toPlace": "B",
                  "journeyDate": "2026-01-01"
                }
                """;

        mvc.perform(post("/api/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].airlineName").value("Air India"));
    }

    @Test
    void getFlightByIdReturnsFlight() throws Exception {
        FlightDTO dto = new FlightDTO(
                1L,
                "Air India",
                "AAA",
                "BBB",
                LocalDate.now().plusDays(1),
                LocalTime.parse("10:00"),
                LocalTime.parse("12:00"),
                3000.0,
                100,
                90
        );
        Mockito.when(flightService.getFlightById(1L))
                .thenReturn(Optional.of(dto));

        mvc.perform(get("/api/flight/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.airlineName").value("Air India"));
    }

    @Test
    void updateFlightInventoryReturnsOk() throws Exception {
        Mockito.when(flightService.updateFlightInventory(any()))
                .thenReturn("Updated");
        String body = """
                {
                  "id": 1,
                  "airlineName": "Air India",
                  "fromPlace": "AAA",
                  "toPlace": "BBB",
                  "scheduleDate": "2026-01-01",
                  "departureTime": "10:00",
                  "arrivalTime": "12:00",
                  "price": 3000,
                  "totalSeats": 100,
                  "availableSeats": 90
                }
                """;

        mvc.perform(put("/api/flight/updateInventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }
}