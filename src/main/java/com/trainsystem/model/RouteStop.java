package com.trainsystem.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "route_stops")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "stop_order", nullable = false)
    private int stopOrder;

    protected RouteStop() {}

    public RouteStop(Route route, Station station, int stopOrder) {
        this.route = route;
        this.station = station;
        this.stopOrder = stopOrder;
    }

    public Long getId() { return id; }

    public Route getRoute() { return route; }

    public Station getStation() { return station; }

    public int getStopOrder() { return stopOrder; }

    public void setStopOrder(int stopOrder) { this.stopOrder = stopOrder; }

    public void setStation(Station station) { this.station = station; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStop)) return false;
        RouteStop routeStop = (RouteStop) o;
        return Objects.equals(id, routeStop.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return stopOrder + ": " + station.getName();
    }
}
