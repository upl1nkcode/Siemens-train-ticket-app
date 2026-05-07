package com.trainsystem.controller.api;

import com.trainsystem.model.Route;
import com.trainsystem.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteApiController.class)
class RouteApiControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private RouteService routeService;

    @Test
    void GetRoutes_ReturnsJsonList() throws Exception {
        Route route = new Route("Line A");
        when(routeService.getAllRoutes()).thenReturn(List.of(route));

        mockMvc.perform(get("/api/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Line A"));
    }

    @Test
    void CreateRoute_Success_ReturnsCreatedRoute() throws Exception {
        Route route = new Route("Line B");
        when(routeService.createRoute(anyString(), anyList())).thenReturn(route);

        mockMvc.perform(post("/api/routes")
                        .param("name", "Line B")
                        .param("stationIds", "1", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Line B"));
    }
}
