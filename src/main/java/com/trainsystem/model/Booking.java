package com.trainsystem.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "passenger_name", nullable = false)
    private String passengerName;

    @Column(name = "passenger_email", nullable = false)
    private String passengerEmail;

    @Column(name = "seats_booked", nullable = false)
    private int seatsBooked;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    protected Booking() {}

    public Booking(Schedule schedule, String passengerName, String passengerEmail, int seatsBooked) {
        this.schedule = schedule;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.seatsBooked = seatsBooked;
        this.bookedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Schedule getSchedule() { return schedule; }

    public String getPassengerName() { return passengerName; }

    public String getPassengerEmail() { return passengerEmail; }

    public int getSeatsBooked() { return seatsBooked; }

    public LocalDateTime getBookedAt() { return bookedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Booking #" + id + " | " + passengerName + " (" + passengerEmail + ")"
                + " | " + seatsBooked + " seat(s) on Schedule #" + schedule.getId();
    }
}
