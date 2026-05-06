package com.trainsystem.service;

import com.trainsystem.exception.NoRouteFoundException;
import com.trainsystem.model.Route;
import com.trainsystem.model.RouteStop;
import com.trainsystem.model.Schedule;
import com.trainsystem.model.Station;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.repository.ScheduleRepository;
import com.trainsystem.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteFinderService {

    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;

    public RouteFinderService(StationRepository stationRepository,
                              RouteRepository routeRepository,
                              ScheduleRepository scheduleRepository) {
        this.stationRepository = stationRepository;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Finds all travel options from origin to destination.
     * Returns a list of journey descriptions, each being either a direct ride
     * or a sequence of changeovers.
     */
    public List<String> findConnections(String fromName, String toName) {
        Station from = stationRepository.findByNameIgnoreCase(fromName)
                .orElseThrow(() -> new com.trainsystem.exception.EntityNotFoundException("Station", fromName));
        Station to = stationRepository.findByNameIgnoreCase(toName)
                .orElseThrow(() -> new com.trainsystem.exception.EntityNotFoundException("Station", toName));

        if (from.equals(to)) {
            throw new IllegalArgumentException("Origin and destination cannot be the same station.");
        }

        List<Route> allRoutes = routeRepository.findAll();

        // Build adjacency: stationId -> set of (neighborStationId, routeId)
        Map<Long, Set<long[]>> adjacency = buildAdjacency(allRoutes);

        // BFS to find all paths (limited depth to avoid infinite loops)
        List<List<Long>> paths = bfs(adjacency, from.getId(), to.getId());

        if (paths.isEmpty()) {
            throw new NoRouteFoundException(fromName, toName);
        }

        return paths.stream()
                .map(path -> describeJourney(path, allRoutes))
                .collect(Collectors.toList());
    }

    private Map<Long, Set<long[]>> buildAdjacency(List<Route> routes) {
        Map<Long, Set<long[]>> adjacency = new HashMap<>();

        for (Route route : routes) {
            List<RouteStop> stops = route.getStops();
            for (int i = 0; i < stops.size() - 1; i++) {
                Long currentId = stops.get(i).getStation().getId();
                Long nextId = stops.get(i + 1).getStation().getId();

                adjacency.computeIfAbsent(currentId, k -> new HashSet<>())
                        .add(new long[]{nextId, route.getId()});
                adjacency.computeIfAbsent(nextId, k -> new HashSet<>())
                        .add(new long[]{currentId, route.getId()});
            }
        }

        return adjacency;
    }

    private List<List<Long>> bfs(Map<Long, Set<long[]>> adjacency, Long startId, Long endId) {
        List<List<Long>> results = new ArrayList<>();
        Queue<List<Long>> queue = new LinkedList<>();
        queue.add(List.of(startId));

        int maxDepth = 10;

        while (!queue.isEmpty()) {
            List<Long> path = queue.poll();

            if (path.size() > maxDepth) continue;

            Long last = path.get(path.size() - 1);

            if (last.equals(endId)) {
                results.add(path);
                continue;
            }

            Set<long[]> neighbors = adjacency.getOrDefault(last, Collections.emptySet());
            for (long[] neighbor : neighbors) {
                Long neighborId = neighbor[0];
                if (!path.contains(neighborId)) {
                    List<Long> newPath = new ArrayList<>(path);
                    newPath.add(neighborId);
                    queue.add(newPath);
                }
            }
        }

        return results;
    }

    private String describeJourney(List<Long> stationIdPath, List<Route> allRoutes) {
        StringBuilder sb = new StringBuilder();

        Map<Long, Station> stationCache = new HashMap<>();
        for (Route route : allRoutes) {
            for (RouteStop stop : route.getStops()) {
                stationCache.put(stop.getStation().getId(), stop.getStation());
            }
        }

        // Consolidate adjacent segments that share the same covering route
        List<String> legs = new ArrayList<>();
        int i = 0;
        while (i < stationIdPath.size() - 1) {
            Long segStart = stationIdPath.get(i);
            Long segEnd = stationIdPath.get(i + 1);
            Route coveringRoute = findCoveringRoute(segStart, segEnd, allRoutes);

            // Extend this leg as far as the same route covers consecutive segments
            int j = i + 1;
            while (j < stationIdPath.size() - 1) {
                Long nextFrom = stationIdPath.get(j);
                Long nextTo = stationIdPath.get(j + 1);
                Route nextRoute = findCoveringRoute(nextFrom, nextTo, allRoutes);
                if (!nextRoute.getId().equals(coveringRoute.getId())) break;
                j++;
            }

            Long legFrom = stationIdPath.get(i);
            Long legTo = stationIdPath.get(j);
            List<Schedule> schedules = scheduleRepository.findByRouteId(coveringRoute.getId());

            String scheduleInfo = schedules.isEmpty()
                    ? "no scheduled departures"
                    : schedules.stream()
                        .map(s -> s.getTrain().getName() + " at " + s.getDepartureTime())
                        .collect(Collectors.joining(", "));

            legs.add(stationCache.get(legFrom).getName() + " -> " + stationCache.get(legTo).getName()
                    + " [" + coveringRoute.getName() + "] (" + scheduleInfo + ")");

            i = j;
        }

        if (legs.size() == 1) {
            sb.append("Direct: ").append(legs.get(0));
        } else {
            sb.append("Changeover (").append(legs.size()).append(" legs):\n");
            for (int k = 0; k < legs.size(); k++) {
                sb.append("  Leg ").append(k + 1).append(": ").append(legs.get(k));
                if (k < legs.size() - 1) sb.append("\n");
            }
        }

        return sb.toString();
    }

    private Route findCoveringRoute(Long fromId, Long toId, List<Route> routes) {
        for (Route route : routes) {
            List<RouteStop> stops = route.getStops();
            boolean foundFrom = false;
            for (RouteStop stop : stops) {
                if (stop.getStation().getId().equals(fromId)) foundFrom = true;
                if (foundFrom && stop.getStation().getId().equals(toId)) return route;
            }
            // Check reverse direction too
            foundFrom = false;
            for (int i = stops.size() - 1; i >= 0; i--) {
                if (stops.get(i).getStation().getId().equals(fromId)) foundFrom = true;
                if (foundFrom && stops.get(i).getStation().getId().equals(toId)) return route;
            }
        }
        return routes.get(0); // fallback (shouldn't happen given BFS found the path)
    }
}
