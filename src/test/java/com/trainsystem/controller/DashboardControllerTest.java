package com.trainsystem.controller;

import com.trainsystem.service.RouteService;
import com.trainsystem.service.ScheduleService;
import com.trainsystem.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RouteService routeService;
    @MockBean private TrainService trainService;
    @MockBean private ScheduleService scheduleService;

    @Test
    void Dashboard_ReturnsViewWithCounts() throws Exception {
        when(routeService.getAllStations()).thenReturn(List.of());
        when(routeService.getAllRoutes()).thenReturn(List.of());
        when(trainService.getAllTrains()).thenReturn(List.of());
        when(scheduleService.getAllSchedules()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists(
                        "stationCount", "routeCount", "trainCount", "scheduleCount"));
    }
}
