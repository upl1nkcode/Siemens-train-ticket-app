package com.trainsystem.controller;

import com.trainsystem.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationController.class)
class StationControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RouteService routeService;

    @Test
    void ListStations_ReturnsStationsView() throws Exception {
        when(routeService.getAllStations()).thenReturn(List.of());

        mockMvc.perform(get("/admin/stations"))
                .andExpect(status().isOk())
                .andExpect(view().name("stations"))
                .andExpect(model().attributeExists("stations"));
    }

    @Test
    void AddStation_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/stations/add")
                        .param("name", "Constanta"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/stations"));

        verify(routeService).createStation("Constanta");
    }

    @Test
    void DeleteStation_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/stations/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/stations"));

        verify(routeService).deleteStation(1L);
    }
}
