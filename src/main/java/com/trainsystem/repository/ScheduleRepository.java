package com.trainsystem.repository;

import com.trainsystem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTrainId(Long trainId);

    List<Schedule> findByRouteId(Long routeId);
}
