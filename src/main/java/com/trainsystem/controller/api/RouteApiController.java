package com.trainsystem.controller.api;

import com.trainsystem.model.Route;
import com.trainsystem.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
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

    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestParam String name, @RequestParam List<Long> stationIds) {
        Route route = routeService.createRoute(name, stationIds);
        return new ResponseEntity<>(route, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Route updateRoute(@PathVariable Long id, @RequestParam String name, @RequestParam List<Long> stationIds) {
        return routeService.updateRoute(id, name, stationIds);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
