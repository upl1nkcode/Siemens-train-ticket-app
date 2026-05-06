package com.trainsystem.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    protected Schedule() {}

    public Schedule(Train train, Route route, LocalDateTime departureTime) {
        this.train = train;
        this.route = route;
        this.departureTime = departureTime;
    }

    public Long getId() { return id; }

    public Train getTrain() { return train; }

    public void setTrain(Train train) { this.train = train; }

    public Route getRoute() { return route; }

    public void setRoute(Route route) { this.route = route; }

    public LocalDateTime getDepartureTime() { return departureTime; }

    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(id, schedule.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Schedule #" + id + " | " + train.getName() + " on " + route.getName()
                + " | Departure: " + departureTime;
    }
}
