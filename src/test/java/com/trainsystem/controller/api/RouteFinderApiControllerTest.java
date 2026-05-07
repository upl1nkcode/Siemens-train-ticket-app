package com.trainsystem.controller.api;

import com.trainsystem.service.RouteFinderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteFinderApiController.class)
class RouteFinderApiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RouteFinderService routeFinderService;

    @Test
    void FindRoutes_ReturnsJson() throws Exception {
        when(routeFinderService.findConnections(anyString(), anyString())).thenReturn(List.of("Direct"));

        mockMvc.perform(get("/api/find-routes")
                        .param("origin", "A")
                        .param("destination", "B"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("A"))
                .andExpect(jsonPath("$.connections[0]").value("Direct"));
    }
}
