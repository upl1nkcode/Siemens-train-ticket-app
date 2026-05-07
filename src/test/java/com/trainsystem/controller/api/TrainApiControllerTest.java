package com.trainsystem.controller.api;

import com.trainsystem.model.Train;
import com.trainsystem.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainApiController.class)
class TrainApiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TrainService trainService;

    @Test
    void GetTrains_ReturnsJsonList() throws Exception {
        Train train = new Train("Express", 100);
        when(trainService.getAllTrains()).thenReturn(List.of(train));

        mockMvc.perform(get("/api/trains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Express"));
    }

    @Test
    void CreateTrain_Success_ReturnsCreatedTrain() throws Exception {
        Train train = new Train("Fast", 150);
        when(trainService.createTrain(anyString(), anyInt())).thenReturn(train);

        mockMvc.perform(post("/api/trains")
                        .param("name", "Fast")
                        .param("totalSeats", "150"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fast"));
    }
}
