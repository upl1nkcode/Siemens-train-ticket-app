package com.trainsystem.controller;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.model.*;
import com.trainsystem.service.BookingService;
import com.trainsystem.service.RouteService;
import com.trainsystem.service.ScheduleService;
import com.trainsystem.service.TrainService;
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

@WebMvcTest(TrainController.class)
class TrainControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TrainService trainService;
    @MockBean private ScheduleService scheduleService;
    @MockBean private RouteService routeService;
    @MockBean private BookingService bookingService;

    @Test
    void ListTrains_ReturnsTrainsView() throws Exception {
        when(trainService.getAllTrains()).thenReturn(List.of());
        when(scheduleService.getAllSchedules()).thenReturn(List.of());
        when(routeService.getAllRoutes()).thenReturn(List.of());

        mockMvc.perform(get("/admin/trains"))
                .andExpect(status().isOk())
                .andExpect(view().name("trains"))
                .andExpect(model().attributeExists("trains", "schedules", "routes"));
    }

    @Test
    void AddTrain_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/trains/add")
                        .param("name", "IR 3000")
                        .param("totalSeats", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/trains"));

        verify(trainService).createTrain("IR 3000", 200);
    }

    @Test
    void AddTrain_ValidationFailure_RedirectsWithErrorMessage() throws Exception {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Train name is required");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        when(trainService.createTrain(anyString(), anyInt())).thenThrow(ex);

        mockMvc.perform(post("/admin/trains/add")
                        .param("name", "")
                        .param("totalSeats", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/trains"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void DeleteTrain_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/trains/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/trains"));

        verify(trainService).deleteTrain(1L);
    }

    @Test
    void AddSchedule_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/trains/schedules/add")
                        .param("trainId", "1")
                        .param("routeId", "1")
                        .param("departureTime", "2026-06-15T08:30:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/trains"));

        verify(scheduleService).createSchedule(eq(1L), eq(1L), any(LocalDateTime.class));
    }

    @Test
    void ReportDelay_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/trains/delay/1")
                        .param("delayMinutes", "30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/trains"));

        verify(trainService).reportDelay(1L, 30);
    }

    @Test
    void ViewBookings_ReturnsBookingsView() throws Exception {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        when(trainService.getTrainById(1L)).thenReturn(train);
        when(bookingService.getBookingsForTrain(1L)).thenReturn(List.of());

        mockMvc.perform(get("/admin/trains/1/bookings"))
                .andExpect(status().isOk())
                .andExpect(view().name("train-bookings"))
                .andExpect(model().attributeExists("train", "bookings"));
    }
}
