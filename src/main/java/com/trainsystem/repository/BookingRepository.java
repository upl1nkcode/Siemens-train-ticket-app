package com.trainsystem.repository;

import com.trainsystem.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByScheduleId(Long scheduleId);

    @Query("SELECT COALESCE(SUM(b.seatsBooked), 0) FROM Booking b WHERE b.schedule.id = :scheduleId")
    int sumSeatsBookedByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT b FROM Booking b WHERE b.schedule.train.id = :trainId")
    List<Booking> findByTrainId(@Param("trainId") Long trainId);
}
