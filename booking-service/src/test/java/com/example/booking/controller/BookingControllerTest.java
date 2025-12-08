package com.example.booking.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.example.booking.entity.Booking;
import com.example.booking.service.BookingService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    BookingService bookingService;

    @Test
    void bookTicketSuccess() throws Exception {
        Mockito.when(bookingService.bookTicket(eq(1L), any()))
                .thenReturn(ResponseEntity.ok("Booked"));

        String body = """
                {
                  "flightId": 1,
                  "userName": "AAA",
                  "userEmail": "AAA@example.com",
                  "journeyDate": "2026-01-01",
                  "mobileNumber": "1234567890",
                  "mealOpted": "Veg",
                  "passengers": [
                    {"name": "AAA", "gender": "Male", "age": 22, "seatNumber": "12A"}
                  ]
                }
                """;

        mvc.perform(post("/api/booking/ticket/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("Booked"));
    }

    @Test
    void getTicketByPnr() throws Exception {
        Booking b = new Booking();
        b.setPnr("XYZ123");
        Mockito.when(bookingService.getTicketByPnr("XYZ123"))
                .thenReturn(b);
        mvc.perform(get("/api/booking/ticket/XYZ123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("XYZ123"));
    }

    @Test
    void getHistoryByEmail() throws Exception {
        Booking b = new Booking();
        b.setPnr("PNR1");
        Mockito.when(bookingService.getBookingHistoryByEmail("a@a.com"))
                .thenReturn(List.of(b));
        mvc.perform(get("/api/booking/booking/history/a@a.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pnr").value("PNR1"));
    }

    @Test
    void cancelTicket() throws Exception {
        mvc.perform(delete("/api/booking/booking/cancel/PNR9"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
