package com.trainsystem.controller;

import com.trainsystem.exception.NoRouteFoundException;
import com.trainsystem.service.RouteFinderService;
import com.trainsystem.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteFinderController.class)
class RouteFinderControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RouteFinderService routeFinderService;
    @MockBean private RouteService routeService;

    @Test
    void ShowFindRoutesForm_ReturnsView() throws Exception {
        when(routeService.getAllStations()).thenReturn(List.of());

        mockMvc.perform(get("/find-routes"))
                .andExpect(status().isOk())
                .andExpect(view().name("find-routes"))
                .andExpect(model().attributeExists("stations"));
    }

    @Test
    void FindRoutes_Success_ReturnsConnections() throws Exception {
        when(routeService.getAllStations()).thenReturn(List.of());
        when(routeFinderService.findConnections("Bucharest", "Brasov"))
                .thenReturn(List.of("Direct: Bucharest -> Brasov"));

        mockMvc.perform(post("/find-routes")
                        .param("origin", "Bucharest")
                        .param("destination", "Brasov"))
                .andExpect(status().isOk())
                .andExpect(view().name("find-routes"))
                .andExpect(model().attributeExists("connections"));
    }

    @Test
    void FindRoutes_NoRoute_ReturnsError() throws Exception {
        when(routeService.getAllStations()).thenReturn(List.of());
        when(routeFinderService.findConnections("Bucharest", "Isolated"))
                .thenThrow(new NoRouteFoundException("Bucharest", "Isolated"));

        mockMvc.perform(post("/find-routes")
                        .param("origin", "Bucharest")
                        .param("destination", "Isolated"))
                .andExpect(status().isOk())
                .andExpect(view().name("find-routes"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void FindRoutes_ValidationFailure_ReturnsError() throws Exception {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Origin station is required");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        when(routeService.getAllStations()).thenReturn(List.of());
        when(routeFinderService.findConnections(anyString(), anyString())).thenThrow(ex);

        mockMvc.perform(post("/find-routes")
                        .param("origin", "")
                        .param("destination", "Brasov"))
                .andExpect(status().isOk())
                .andExpect(view().name("find-routes"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
