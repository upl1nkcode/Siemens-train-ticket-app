package com.trainsystem.controller.api;

import com.trainsystem.model.Schedule;
import com.trainsystem.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
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

    @PostMapping
    public ResponseEntity<Schedule> createSchedule(
            @RequestParam Long trainId,
            @RequestParam Long routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime) {
        Schedule schedule = scheduleService.createSchedule(trainId, routeId, departureTime);
        return new ResponseEntity<>(schedule, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
