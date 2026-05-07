package com.trainsystem.controller.api;

import com.trainsystem.model.Route;
import com.trainsystem.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Validated
public class RouteApiController {

    private final RouteService routeService;

    public RouteApiController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeService.getAllRoutes();
    }

    @GetMapping("/{id}")
    public Route getRoute(@PathVariable Long id) {
        return routeService.getRouteById(id);
    }

    public static class RouteRequest {
        @NotBlank(message = "Route name is required")
        private String name;
        
        @NotEmpty(message = "Route must have at least one station")
        private List<Long> stationIds;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Long> getStationIds() { return stationIds; }
        public void setStationIds(List<Long> stationIds) { this.stationIds = stationIds; }
    }

    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody @Valid RouteRequest req) {
        Route route = routeService.createRoute(req.getName(), req.getStationIds());
        return new ResponseEntity<>(route, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Route updateRoute(@PathVariable Long id, @RequestBody @Valid RouteRequest req) {
        return routeService.updateRoute(id, req.getName(), req.getStationIds());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
