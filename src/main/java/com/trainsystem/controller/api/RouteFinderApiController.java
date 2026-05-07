package com.trainsystem.controller.api;

import com.trainsystem.service.RouteFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/find-routes")
public class RouteFinderApiController {

    private final RouteFinderService routeFinderService;

    public RouteFinderApiController(RouteFinderService routeFinderService) {
        this.routeFinderService = routeFinderService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findRoutes(
            @RequestParam String origin,
            @RequestParam String destination) {
        List<String> connections = routeFinderService.findConnections(origin, destination);
        Map<String, Object> response = new HashMap<>();
        response.put("origin", origin);
        response.put("destination", destination);
        response.put("connections", connections);
        return ResponseEntity.ok(response);
    }
}
