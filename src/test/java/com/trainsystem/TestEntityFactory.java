package com.trainsystem;

import com.trainsystem.model.*;
import com.trainsystem.model.enums.TrainStatus;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * Builds domain entities with IDs set via reflection.
 * Production models intentionally have no ID setters, so tests use this factory
 * instead of polluting the domain API.
 */
public final class TestEntityFactory {

    private TestEntityFactory() {}

    public static Station station(Long id, String name) {
        Station station = new Station(name);
        setId(station, id);
        return station;
    }

    public static Train train(Long id, String name, int totalSeats) {
        Train train = new Train(name, totalSeats);
        setId(train, id);
        return train;
    }

    public static Route route(Long id, String name) {
        Route route = new Route(name);
        setId(route, id);
        return route;
    }

    public static RouteStop routeStop(Long id, Route route, Station station, int order) {
        RouteStop stop = new RouteStop(route, station, order);
        setId(stop, id);
        return stop;
    }

    public static Schedule schedule(Long id, Train train, Route route, LocalDateTime departure) {
        Schedule schedule = new Schedule(train, route, departure);
        setId(schedule, id);
        return schedule;
    }

    public static Booking booking(Long id, Schedule schedule, String name, String email, int seats) {
        Booking booking = new Booking(schedule, name, email, seats);
        setId(booking, id);
        return booking;
    }

    private static void setId(Object entity, Long id) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to set ID on " + entity.getClass().getSimpleName(), e);
        }
    }
}
