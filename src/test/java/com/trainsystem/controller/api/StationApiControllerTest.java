package com.trainsystem.controller.api;

import com.trainsystem.model.Station;
import com.trainsystem.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StationApiController.class)
class StationApiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RouteService routeService;

    @Test
    void GetStations_ReturnsJsonList() throws Exception {
        Station station = new Station("Paris");
        when(routeService.getAllStations()).thenReturn(List.of(station));

        mockMvc.perform(get("/api/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paris"));
    }

    @Test
    void CreateStation_Success_ReturnsCreatedStation() throws Exception {
        Station station = new Station("London");
        when(routeService.createStation(anyString())).thenReturn(station);

        mockMvc.perform(post("/api/stations")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"London\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("London"));
    }
}
