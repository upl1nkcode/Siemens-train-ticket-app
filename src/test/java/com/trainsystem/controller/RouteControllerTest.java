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

@WebMvcTest(RouteController.class)
class RouteControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RouteService routeService;

    @Test
    void ListRoutes_ReturnsRoutesView() throws Exception {
        when(routeService.getAllRoutes()).thenReturn(List.of());
        when(routeService.getAllStations()).thenReturn(List.of());

        mockMvc.perform(get("/admin/routes"))
                .andExpect(status().isOk())
                .andExpect(view().name("routes"))
                .andExpect(model().attributeExists("routes", "stations"));
    }

    @Test
    void AddRoute_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/routes/add")
                        .param("name", "New Route")
                        .param("stationIds", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/routes"));

        verify(routeService).createRoute(eq("New Route"), eq(List.of(1L, 2L)));
    }

    @Test
    void AddRoute_EmptyStations_RedirectsWithError() throws Exception {
        mockMvc.perform(post("/admin/routes/add")
                        .param("name", "Bad Route"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/routes"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(routeService, never()).createRoute(anyString(), anyList());
    }

    @Test
    void DeleteRoute_Success_Redirects() throws Exception {
        mockMvc.perform(post("/admin/routes/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/routes"));

        verify(routeService).deleteRoute(1L);
    }
}
