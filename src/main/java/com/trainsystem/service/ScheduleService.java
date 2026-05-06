package com.trainsystem.service;

import com.trainsystem.exception.EntityNotFoundException;
import com.trainsystem.model.Schedule;
import com.trainsystem.model.Route;
import com.trainsystem.model.Train;
import com.trainsystem.repository.RouteRepository;
import com.trainsystem.repository.ScheduleRepository;
import com.trainsystem.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           TrainRepository trainRepository,
                           RouteRepository routeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.trainRepository = trainRepository;
        this.routeRepository = routeRepository;
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new EntityNotFoundException("Schedule", id));
    }

    @Transactional
    public Schedule createSchedule(Long trainId, Long routeId, LocalDateTime departureTime) {
        Train train = trainRepository.findById(Objects.requireNonNull(trainId))
                .orElseThrow(() -> new EntityNotFoundException("Train", trainId));
        Route route = routeRepository.findById(Objects.requireNonNull(routeId))
                .orElseThrow(() -> new EntityNotFoundException("Route", routeId));

        Schedule schedule = new Schedule(train, route, departureTime);
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(Objects.requireNonNull(schedule));
    }

    public List<Schedule> getSchedulesForTrain(Long trainId) {
        return scheduleRepository.findByTrainId(trainId);
    }
}
