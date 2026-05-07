package com.trainsystem.controller.api;

import com.trainsystem.model.Schedule;
import com.trainsystem.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@Validated
public class ScheduleApiController {

    private final ScheduleService scheduleService;

    public ScheduleApiController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public Schedule getSchedule(@PathVariable Long id) {
        return scheduleService.getScheduleById(id);
    }

    public static class ScheduleRequest {
        @NotNull(message = "Train ID is required")
        private Long trainId;

        @NotNull(message = "Route ID is required")
        private Long routeId;

        @NotNull(message = "Departure time is required")
        private LocalDateTime departureTime;

        public Long getTrainId() { return trainId; }
        public void setTrainId(Long trainId) { this.trainId = trainId; }
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        public LocalDateTime getDepartureTime() { return departureTime; }
        public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    }

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody @Valid ScheduleRequest req) {
        Schedule schedule = scheduleService.createSchedule(req.getTrainId(), req.getRouteId(), req.getDepartureTime());
        return new ResponseEntity<>(schedule, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
