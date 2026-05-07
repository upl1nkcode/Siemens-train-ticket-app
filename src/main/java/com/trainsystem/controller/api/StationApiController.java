package com.trainsystem.controller.api;

import com.trainsystem.model.Station;
import com.trainsystem.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationApiController {

    private final RouteService routeService;

    public StationApiController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public List<Station> getAllStations() {
        return routeService.getAllStations();
    }

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestParam String name) {
        Station station = routeService.createStation(name);
        return new ResponseEntity<>(station, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        routeService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
