package com.trainsystem.controller;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.exception.OverbookingException;
import com.trainsystem.model.*;
import com.trainsystem.service.BookingService;
import com.trainsystem.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BookingService bookingService;
    @MockBean private ScheduleService scheduleService;

    @Test
    void ShowBookForm_ReturnsBookView() throws Exception {
        when(scheduleService.getAllSchedules()).thenReturn(List.of());

        mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("schedules"));
    }

    @Test
    void BookTickets_Success_RedirectsWithSuccessMessage() throws Exception {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        Schedule schedule = TestEntityFactory.schedule(1L, train, route,
                LocalDateTime.of(2026, 6, 15, 8, 30));
        Booking booking = TestEntityFactory.booking(10L, schedule, "Andrei", "a@test.com", 2);

        when(bookingService.bookTickets(eq(1L), eq("Andrei"), eq("a@test.com"), eq(2)))
                .thenReturn(booking);

        mockMvc.perform(post("/book")
                        .param("scheduleId", "1")
                        .param("passengerName", "Andrei")
                        .param("email", "a@test.com")
                        .param("seats", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void BookTickets_Overbooking_RedirectsWithErrorMessage() throws Exception {
        when(bookingService.bookTickets(anyLong(), anyString(), anyString(), anyInt()))
                .thenThrow(new OverbookingException(5, 2));

        mockMvc.perform(post("/book")
                        .param("scheduleId", "1")
                        .param("passengerName", "Andrei")
                        .param("email", "a@test.com")
                        .param("seats", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void BookTickets_ValidationFailure_RedirectsWithErrorMessage() throws Exception {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Passenger name is required");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        when(bookingService.bookTickets(anyLong(), anyString(), anyString(), anyInt()))
                .thenThrow(ex);

        mockMvc.perform(post("/book")
                        .param("scheduleId", "1")
                        .param("passengerName", "")
                        .param("email", "a@test.com")
                        .param("seats", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/book"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
