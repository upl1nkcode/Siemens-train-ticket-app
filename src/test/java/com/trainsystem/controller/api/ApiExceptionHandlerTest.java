package com.trainsystem.controller.api;

import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.exception.OverbookingException;
import com.trainsystem.service.TrainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainApiController.class)
class ApiExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TrainService trainService;

    @Test
    void HandleEntityNotFound_Returns404() throws Exception {
        when(trainService.getTrainById(anyLong())).thenThrow(new EntityNotFoundException("Train", 1L));

        mockMvc.perform(get("/api/trains/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Train not found with identifier: 1"));
    }

    @Test
    void HandleBadRequest_Returns400() throws Exception {
        when(trainService.getTrainById(anyLong())).thenThrow(new OverbookingException(5, 2));

        mockMvc.perform(get("/api/trains/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot book 5 seat(s) — only 2 available."));
    }
}
