package com.trainsystem.service;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.exception.NoRouteFoundException;
import com.trainsystem.model.*;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.repository.ScheduleRepository;
import com.trainsystem.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteFinderServiceTest {

    @Mock private StationRepository stationRepo;
    @Mock private RouteRepository routeRepo;
    @Mock private ScheduleRepository scheduleRepo;

    @InjectMocks private RouteFinderService routeFinderService;

    private Station bucharest, brasov, clujNapoca, sibiu;

    @BeforeEach
    void setUp() {
        bucharest = TestEntityFactory.station(1L, "Bucharest");
        brasov = TestEntityFactory.station(2L, "Brasov");
        clujNapoca = TestEntityFactory.station(3L, "Cluj-Napoca");
        sibiu = TestEntityFactory.station(5L, "Sibiu");
    }

    @Test
    void FindConnections_DirectRoute_ReturnsSingleLeg() {
        Route route = buildRoute(1L, "Bucharest - Brasov", bucharest, brasov);

        when(stationRepo.findByNameIgnoreCase("Bucharest")).thenReturn(Optional.of(bucharest));
        when(stationRepo.findByNameIgnoreCase("Brasov")).thenReturn(Optional.of(brasov));
        when(routeRepo.findAll()).thenReturn(List.of(route));

        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Schedule schedule = TestEntityFactory.schedule(1L, train, route,
                LocalDateTime.of(2026, 6, 15, 8, 30));
        when(scheduleRepo.findByRouteId(1L)).thenReturn(List.of(schedule));

        List<String> connections = routeFinderService.findConnections("Bucharest", "Brasov");

        assertThat(connections).hasSize(1);
        assertThat(connections.get(0)).startsWith("Direct:");
    }

    @Test
    void FindConnections_ChangeoverRequired_ReturnsMultiLeg() {
        Route route1 = buildRoute(1L, "Bucharest - Brasov", bucharest, brasov);
        Route route2 = buildRoute(2L, "Brasov - Cluj via Sibiu", brasov, sibiu, clujNapoca);

        when(stationRepo.findByNameIgnoreCase("Bucharest")).thenReturn(Optional.of(bucharest));
        when(stationRepo.findByNameIgnoreCase("Cluj-Napoca")).thenReturn(Optional.of(clujNapoca));
        when(routeRepo.findAll()).thenReturn(List.of(route1, route2));
        when(scheduleRepo.findByRouteId(anyLong())).thenReturn(Collections.emptyList());

        List<String> connections = routeFinderService.findConnections("Bucharest", "Cluj-Napoca");

        assertThat(connections).isNotEmpty();
        assertThat(connections.get(0)).contains("Changeover");
    }

    @Test
    void FindConnections_NoPath_ThrowsNoRouteFoundException() {
        Station isolated = TestEntityFactory.station(99L, "Isolated");
        Route route = buildRoute(1L, "Bucharest - Brasov", bucharest, brasov);

        when(stationRepo.findByNameIgnoreCase("Bucharest")).thenReturn(Optional.of(bucharest));
        when(stationRepo.findByNameIgnoreCase("Isolated")).thenReturn(Optional.of(isolated));
        when(routeRepo.findAll()).thenReturn(List.of(route));

        assertThatThrownBy(() -> routeFinderService.findConnections("Bucharest", "Isolated"))
                .isInstanceOf(NoRouteFoundException.class);
    }

    @Test
    void FindConnections_SameStation_ThrowsIllegalArgument() {
        when(stationRepo.findByNameIgnoreCase("Bucharest")).thenReturn(Optional.of(bucharest));

        assertThatThrownBy(() -> routeFinderService.findConnections("Bucharest", "Bucharest"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same station");
    }

    @Test
    void FindConnections_StationNotFound_ThrowsEntityNotFoundException() {
        when(stationRepo.findByNameIgnoreCase("Nowhere")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeFinderService.findConnections("Nowhere", "Brasov"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Route buildRoute(Long id, String name, Station... stations) {
        Route route = TestEntityFactory.route(id, name);
        for (int i = 0; i < stations.length; i++) {
            RouteStop stop = TestEntityFactory.routeStop(
                    (long) (id * 100 + i), route, stations[i], i + 1);
            route.getStops().add(stop);
        }
        return route;
    }
}
