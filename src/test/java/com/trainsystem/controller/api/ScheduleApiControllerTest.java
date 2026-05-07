package com.trainsystem.controller.api;

import com.trainsystem.model.Route;
import com.trainsystem.model.Schedule;
import com.trainsystem.model.Train;
import com.trainsystem.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleApiController.class)
class ScheduleApiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ScheduleService scheduleService;

    @Test
    void GetSchedules_ReturnsJsonList() throws Exception {
        Schedule schedule = new Schedule(new Train("T1", 100), new Route("R1"), LocalDateTime.now());
        when(scheduleService.getAllSchedules()).thenReturn(List.of(schedule));

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].train.name").value("T1"));
    }

    @Test
    void CreateSchedule_Success_ReturnsCreatedSchedule() throws Exception {
        Schedule schedule = new Schedule(new Train("T1", 100), new Route("R1"), LocalDateTime.of(2026, 10, 10, 10, 0));
        when(scheduleService.createSchedule(anyLong(), anyLong(), any())).thenReturn(schedule);

        mockMvc.perform(post("/api/schedules")
                        .param("trainId", "1")
                        .param("routeId", "1")
                        .param("departureTime", "2026-10-10T10:00:00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.train.name").value("T1"));
    }
}
