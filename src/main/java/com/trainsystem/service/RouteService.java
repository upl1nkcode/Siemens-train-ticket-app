package com.trainsystem.service;

import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.model.Route;
import com.trainsystem.model.Station;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

@Service
@Validated
public class RouteService {

    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;

    public RouteService(RouteRepository routeRepository, StationRepository stationRepository) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("Route", id));
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @Transactional
    public Station createStation(@NotBlank(message = "Station name is required") String name) {
        if (stationRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Station '" + name + "' already exists.");
        }
        return stationRepository.save(new Station(name));
    }

    @Transactional
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("Station", id));
        stationRepository.delete(Objects.requireNonNull(station));
    }

    @Transactional
    public Route createRoute(@NotBlank(message = "Route name is required") String name, 
                             @NotEmpty(message = "At least one station must be selected") List<Long> stationIds) {
        Route route = new Route(name);
        for (int i = 0; i < stationIds.size(); i++) {
            final Long stationId = stationIds.get(i);
            Station station = stationRepository.findById(Objects.requireNonNull(stationId))
                    .orElseThrow(() -> new EntityNotFoundException("Station", stationId));
            route.addStop(station, i + 1);
        }
        return routeRepository.save(route);
    }

    @Transactional
    public Route updateRoute(Long id, 
                             @NotBlank(message = "Route name is required") String name, 
                             @NotEmpty(message = "At least one station must be selected") List<Long> stationIds) {
        Route route = getRouteById(id);
        route.setName(name);
        route.getStops().clear();
        for (int i = 0; i < stationIds.size(); i++) {
            final Long stationId = stationIds.get(i);
            Station station = stationRepository.findById(Objects.requireNonNull(stationId))
                    .orElseThrow(() -> new EntityNotFoundException("Station", stationId));
            route.addStop(station, i + 1);
        }
        return routeRepository.save(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        Route route = getRouteById(id);
        routeRepository.delete(Objects.requireNonNull(route));
    }
}
