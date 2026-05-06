package com.trainsystem.model;

import com.trainsystem.model.enums.TrainStatus;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainStatus status = TrainStatus.ON_TIME;

    @Column(name = "delay_minutes")
    private int delayMinutes = 0;

    protected Train() {}

    public Train(String name, int totalSeats) {
        this.name = name;
        this.totalSeats = totalSeats;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getTotalSeats() { return totalSeats; }

    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public TrainStatus getStatus() { return status; }

    public void setStatus(TrainStatus status) { this.status = status; }

    public int getDelayMinutes() { return delayMinutes; }

    public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Train)) return false;
        Train train = (Train) o;
        return Objects.equals(id, train.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return name + " (" + totalSeats + " seats, " + status + ")";
    }
}
