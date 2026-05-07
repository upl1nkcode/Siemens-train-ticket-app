package com.trainsystem.controller.api;

import com.trainsystem.model.Booking;
import com.trainsystem.model.Route;
import com.trainsystem.model.Schedule;
import com.trainsystem.model.Train;
import com.trainsystem.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingApiController.class)
class BookingApiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BookingService bookingService;

    @Test
    void GetBookingsForSchedule_ReturnsJsonList() throws Exception {
        Schedule schedule = new Schedule(new Train("T", 100), new Route("R"), LocalDateTime.now());
        Booking booking = new Booking(schedule, "Alice", "alice@test.com", 2);
        when(bookingService.getBookingsForSchedule(anyLong())).thenReturn(List.of(booking));

        mockMvc.perform(get("/api/bookings/schedule/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passengerName").value("Alice"));
    }

    @Test
    void CreateBooking_Success_ReturnsCreatedBooking() throws Exception {
        Schedule schedule = new Schedule(new Train("T", 100), new Route("R"), LocalDateTime.now());
        Booking booking = new Booking(schedule, "Bob", "bob@test.com", 1);
        when(bookingService.bookTickets(anyLong(), anyString(), anyString(), anyInt())).thenReturn(booking);

        mockMvc.perform(post("/api/bookings")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"scheduleId\": 1, \"passengerName\": \"Bob\", \"email\": \"bob@test.com\", \"seats\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passengerName").value("Bob"));
    }
}
