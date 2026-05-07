package com.trainsystem.service;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.model.Route;
import com.trainsystem.model.Station;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock private RouteRepository routeRepo;
    @Mock private StationRepository stationRepo;

    @InjectMocks private RouteService routeService;

    @Test
    void GetAllRoutes_ReturnsList() {
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        when(routeRepo.findAll()).thenReturn(List.of(route));

        List<Route> result = routeService.getAllRoutes();

        assertThat(result).hasSize(1);
    }

    @Test
    void GetRouteById_Exists_ReturnsRoute() {
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        when(routeRepo.findById(1L)).thenReturn(Optional.of(route));

        Route result = routeService.getRouteById(1L);

        assertThat(result.getName()).isEqualTo("Bucharest - Brasov");
    }

    @Test
    void GetRouteById_NotFound_ThrowsEntityNotFoundException() {
        when(routeRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.getRouteById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void GetAllStations_ReturnsList() {
        Station station = TestEntityFactory.station(1L, "Bucharest");
        when(stationRepo.findAll()).thenReturn(List.of(station));

        List<Station> result = routeService.getAllStations();

        assertThat(result).hasSize(1);
    }

    @Test
    void CreateStation_NewName_SavesStation() {
        when(stationRepo.existsByNameIgnoreCase("Constanta")).thenReturn(false);
        when(stationRepo.save(any(Station.class))).thenAnswer(invocation -> {
            Station saved = invocation.getArgument(0);
            return TestEntityFactory.station(10L, saved.getName());
        });

        Station result = routeService.createStation("Constanta");

        assertThat(result.getName()).isEqualTo("Constanta");
        verify(stationRepo).save(any(Station.class));
    }

    @Test
    void CreateStation_DuplicateName_ThrowsIllegalArgument() {
        when(stationRepo.existsByNameIgnoreCase("Bucharest")).thenReturn(true);

        assertThatThrownBy(() -> routeService.createStation("Bucharest"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void DeleteStation_Exists_Deletes() {
        Station station = TestEntityFactory.station(1L, "Bucharest");
        when(stationRepo.findById(1L)).thenReturn(Optional.of(station));

        routeService.deleteStation(1L);

        verify(stationRepo).delete(station);
    }

    @Test
    void DeleteStation_NotFound_ThrowsEntityNotFoundException() {
        when(stationRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.deleteStation(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void CreateRoute_WithValidStations_BuildsStopsInOrder() {
        Station bucharest = TestEntityFactory.station(1L, "Bucharest");
        Station brasov = TestEntityFactory.station(2L, "Brasov");

        when(stationRepo.findById(1L)).thenReturn(Optional.of(bucharest));
        when(stationRepo.findById(2L)).thenReturn(Optional.of(brasov));
        when(routeRepo.save(any(Route.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Route result = routeService.createRoute("Test Route", List.of(1L, 2L));

        assertThat(result.getStops()).hasSize(2);
        assertThat(result.getStops().get(0).getStation().getName()).isEqualTo("Bucharest");
        assertThat(result.getStops().get(0).getStopOrder()).isEqualTo(1);
        assertThat(result.getStops().get(1).getStation().getName()).isEqualTo("Brasov");
        assertThat(result.getStops().get(1).getStopOrder()).isEqualTo(2);
    }

    @Test
    void CreateRoute_WithInvalidStationId_ThrowsEntityNotFoundException() {
        when(stationRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.createRoute("Bad Route", List.of(999L)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void UpdateRoute_ClearsOldStopsAndAddsNew() {
        Station bucharest = TestEntityFactory.station(1L, "Bucharest");
        Station sibiu = TestEntityFactory.station(5L, "Sibiu");

        Route existing = TestEntityFactory.route(1L, "Old Route");
        existing.addStop(bucharest, 1);

        when(routeRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(stationRepo.findById(5L)).thenReturn(Optional.of(sibiu));
        when(routeRepo.save(any(Route.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Route result = routeService.updateRoute(1L, "Updated Route", List.of(5L));

        assertThat(result.getName()).isEqualTo("Updated Route");
        assertThat(result.getStops()).hasSize(1);
        assertThat(result.getStops().get(0).getStation().getName()).isEqualTo("Sibiu");
    }

    @Test
    void DeleteRoute_Exists_Deletes() {
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        when(routeRepo.findById(1L)).thenReturn(Optional.of(route));

        routeService.deleteRoute(1L);

        verify(routeRepo).delete(route);
    }
}
