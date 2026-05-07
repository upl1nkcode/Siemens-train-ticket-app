package com.trainsystem.controller.api;

import com.trainsystem.model.Station;
import com.trainsystem.service.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
@Validated
public class StationApiController {

    private final RouteService routeService;

    public StationApiController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public List<Station> getAllStations() {
        return routeService.getAllStations();
    }

    public static class StationRequest {
        @NotBlank(message = "Station name is required")
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody @Valid StationRequest req) {
        Station station = routeService.createStation(req.getName());
        return new ResponseEntity<>(station, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        routeService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
